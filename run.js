const { spawn } = require('child_process');
const path = require('path');
const mode = process.argv[2] || 'development';

console.log(`Starting application in ${mode} mode...\n`);

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
                console.error("Java installation failed. Please install it manually from:");
                console.error("Oracle: https://www.oracle.com/java/");
                console.error("AdoptOpenJDK: https://adoptopenjdk.net/");
                process.exit(1);
            }
        });
    } else if (process.platform === 'darwin') {
        console.error("Java is required but not installed. Please install it via Homebrew with `brew install openjdk`.");
        process.exit(1);
    } else {
        console.error("Java is required but not installed. Please install it manually with your package manager.");
        process.exit(1);
    }
}

function installNode() {
    if (process.platform === 'win32') {
        console.log("Node.js not found. Attempting to install with Chocolatey...");
        const installProcess = spawn('choco', ['install', 'nodejs', '-y'], { shell: true, stdio: 'inherit' });

        installProcess.on('close', (code) => {
            if (code === 0) {
                console.log("Node.js installed successfully.");
                startFrontend();
            } else {
                console.error("Node.js installation failed. Please install it manually from:");
                console.error("https://nodejs.org/");
                process.exit(1);
            }
        });
    } else if (process.platform === 'darwin') {
        console.error("Node.js is required but not installed. Please install it via Homebrew with `brew install node`.");
        process.exit(1);
    } else {
        console.error("Node.js is required but not installed. Please install it manually with your package manager.");
        process.exit(1);
    }
}

function startBackend() {
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
        const buildProcess = spawn('npm', ['run', 'prod'], {
            cwd: frontendPath,
            shell: true,
            stdio: 'inherit',
        });

        buildProcess.on('close', (code) => {
            if (code === 0) {
                console.log('React app built successfully. Copying files to Spring Boot static directory...');

                const copyCommand = process.platform === 'win32' ? 'xcopy' : 'cp';
                const copyArgs = process.platform === 'win32'
                    ? ['/s', '/e', 'build', path.join('..', '.', 'src', 'main', 'resources', 'static')]
                    : ['-r', 'build/*', path.join('..', '.', 'src', 'main', 'resources', 'static')];

                const copyProcess = spawn(copyCommand, copyArgs, {
                    cwd: frontendPath,
                    shell: true,
                    stdio: 'inherit',
                });

                copyProcess.on('close', (copyCode) => {
                    if (copyCode === 0) {
                        console.log('Files copied successfully.');
                        startBackend();
                    } else {
                        console.error(`Error copying files with exit code ${copyCode}`);
                    }
                });
            } else {
                console.error(`React build process exited with code ${code}`);
            }
        });
    } else {
        console.log('Starting React app in development mode...');


        frontendProcess = spawn('npm', ['dev'], {
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

// Check if Java is installed
checkInstallation(
    'java',
    ['-version'],
    () => {
        console.log('Java is installed.');

        // Check if Node.js is installed after confirming Java
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

// Handle process termination for clean exit
process.on('SIGINT', () => {
    console.log('\nShutting down...');

    if (backendProcess) backendProcess.kill('SIGINT');
    if (frontendProcess) frontendProcess.kill('SIGINT');

    process.exit();
});
