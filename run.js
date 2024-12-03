const { spawn } = require('child_process');
const path = require('path');
const mode = process.argv[2] || 'development';

console.log(`Starting application in ${mode} mode...\n`);

const { exec } = require("child_process");

const ports = [8080, 3000];
let killPromises = [];

// Function to kill processes on a port
function killProcess(port) {
    return new Promise((resolve) => {
        exec(`lsof -i :${port} | grep LISTEN`, (err, stdout) => {
            if (err || !stdout.trim()) {
                console.log(`Port ${port} is free.`);
                resolve();
                return;
            }

            // Extract PIDs from the output
            const pids = stdout
                .split("\n") // Split by lines
                .map((line) => line.split(/\s+/)[1]) // Extract the PID column
                .filter((pid) => pid); // Filter out empty entries

            if (pids.length > 0) {
                console.log(`Processes running on port ${port}: ${pids.join(", ")}`);
                const killPromises = pids.map(
                    (pid) =>
                        new Promise((killResolve) => {
                            exec(`kill -9 ${pid}`, (killErr) => {
                                if (killErr) {
                                    console.error(`Failed to kill process ${pid} on port ${port}:`, killErr);
                                } else {
                                    console.log(`Successfully killed process ${pid} on port ${port}`);
                                }
                                killResolve();
                            });
                        })
                );

                Promise.all(killPromises).then(resolve);
            } else {
                resolve();
            }
        });
    });
}

// Kill processes on all ports
ports.forEach((port) => {
    killPromises.push(killProcess(port));
});

// Add a delay to ensure ports are released
Promise.all(killPromises).then(() => {
    console.log("Waiting for ports to be released...");
    setTimeout(() => {
        console.log("Ports released. Ready to start the application.");
        process.exit(0); // Exit the script
    }, 10000); // 3-second delay
});

let backendProcess, frontendProcess;

function checkInstallation(command, args, onSuccess, onFailure) {
    const checkProcess = spawn(command, args, { shell: true });

    checkProcess.on('error', () => onFailure());
    checkProcess.on('close', (code) => {
        if (code === 0) {
            onSuccess();
        } else {
            onFailure();
        }
    });
}

function installJava() {
    if (process.platform === 'win32') {
        console.log("Java not found. Attempting to install with Chocolatey...");
        const installProcess = spawn('choco', ['install', 'openjdk', '-y'], { shell: true, stdio: 'inherit' });

        installProcess.on('close', (code) => {
            if (code === 0) {
                console.log("Java installed successfully.");
                startFrontend();
            } else {
                console.error("Java installation failed. Please install it manually:");
                console.error("Oracle: https://www.oracle.com/java/");
                console.error("AdoptOpenJDK: https://adoptopenjdk.net/");
                process.exit(1);
            }
        });
    } else if (process.platform === 'darwin') {
        console.error("Please install Java via Homebrew with `brew install openjdk`.");
        process.exit(1);
    } else {
        console.error("Java is required. Install it manually with your package manager.");
        process.exit(1);
    }
}

function installNode() {
    if (process.platform === 'win32') {
        console.log("Node.js not found. Installing with Chocolatey...");
        const installProcess = spawn('choco', ['install', 'nodejs', '-y'], { shell: true, stdio: 'inherit' });

        installProcess.on('close', (code) => {
            if (code === 0) {
                console.log("Node.js installed successfully.");
                startFrontend();
            } else {
                console.error("Node.js installation failed. Install it manually:");
                console.error("https://nodejs.org/");
                process.exit(1);
            }
        });
    } else if (process.platform === 'darwin') {
        console.error("Please install Node.js via Homebrew with `brew install node`.");
        process.exit(1);
    } else {
        console.error("Node.js is required. Install it manually with your package manager.");
        process.exit(1);
    }
}

function startBackend() {
    console.log("Starting backend server...");
    const backendPath = path.join(__dirname, '.');
    const backendCommand = process.platform === 'win32' ? 'gradlew.bat' : './gradlew';
    const backendArgs = ['bootRun'];

    if (mode === 'production') {
        backendArgs.push('-Pprod');
    }

    backendProcess = spawn(backendCommand, backendArgs, {
        cwd: backendPath,
        shell: true,
        stdio: 'inherit',
    });

    backendProcess.on('error', (err) => {
        console.error(`Backend process error: ${err.message}`);
    });

    backendProcess.on('close', (code) => {
        console.log(`Backend process exited with code ${code}`);
    });
}

function startFrontend() {
    const frontendPath = path.join(__dirname, 'frontend');

    if (mode === 'production') {
        console.log('Building React app for production...');
        const buildProcess = spawn('npm', ['run', 'build'], {
            cwd: frontendPath,
            shell: true,
            stdio: 'inherit',
        });

        buildProcess.on('close', (code) => {
            if (code === 0) {
                console.log('React app built successfully. Starting backend server...');
                startBackend();
            } else {
                console.error(`React build process exited with code ${code}`);
            }
        });
    } else {
        console.log('Starting React app in development mode...');
        frontendProcess = spawn('npm', ['start'], {
            cwd: frontendPath,
            shell: true,
            stdio: 'inherit',
        });

        frontendProcess.on('error', (err) => {
            console.error(`Frontend process error: ${err.message}`);
        });

        frontendProcess.on('close', (code) => {
            console.log(`Frontend process exited with code ${code}`);
        });

        startBackend();
    }
}

// Check for Java installation
checkInstallation(
    'java',
    ['-version'],
    () => {
        console.log('Java is installed.');

        // Check for Node.js after Java verification
        checkInstallation(
            'node',
            ['-v'],
            () => {
                console.log('Node.js is installed.');
                startFrontend();
            },
            installNode
        );
    },
    installJava
);

// Gracefully handle process termination
process.on('SIGINT', () => {
    console.log('\nShutting down...');

    if (backendProcess) backendProcess.kill('SIGINT');
    if (frontendProcess) frontendProcess.kill('SIGINT');

    process.exit();
});