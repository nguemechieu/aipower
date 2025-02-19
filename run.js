const { spawn, exec } = require('child_process');
const path = require('path');

const mode = process.argv[2] || 'development';
console.log(`[INFO] Starting application in ${mode} mode...\n`);

const ports = [8080, 3000];
let backendProcess, frontendProcess;

// Function to kill processes running on a specific port
function killProcess(port) {
    return new Promise((resolve, reject) => {
        exec(`lsof -ti:${port}`, (err, stdout) => {
            if (err || !stdout.trim()) {
                console.log(`[INFO] Port ${port} is free.`);
                resolve();
                return;
            }

            const pids = stdout.trim().split('\n');
            console.log(`[INFO] Killing processes on port ${port}: ${pids.join(', ')}`);

            Promise.all(pids.map(pid => execPromise(`kill -9 ${pid}`)))
                .then(() => resolve())
                .catch(reject);
        });
    });
}

function execPromise(command) {
    return new Promise((resolve, reject) => {
        exec(command, (error, stdout, stderr) => {
            if (error) {
                reject(error);
            } else {
                resolve(stdout.trim());
            }
        });
    });
}

// Free all required ports
async function freePorts() {
    console.log('[INFO] Checking and freeing ports...');
    await Promise.all(ports.map(killProcess));
    console.log('[INFO] Ports are now free.');
}

// Install Java
function installJava() {
    checkInstallation(
        'java',
        ['-version'],
        () => {
            console.log('[INFO] Java is already installed.');
        },
        () => {
            console.error('[ERROR] Java not found.');
            console.log('[INFO] Attempting to install the latest version of Java...');

            performJavaInstallation((success) => {
                if (success) {
                    console.log('[INFO] Java installed successfully. Rechecking installation...');
                    installJava(); // Recheck to confirm successful installation
                } else {
                    console.error('[ERROR] Java installation failed. Please install it manually.');
                    console.log(' - Oracle: https://www.oracle.com/java/');
                    console.log(' - AdoptOpenJDK: https://adoptopenjdk.net/');
                }
            });
        }
    );
}

function performJavaInstallation(callback) {
    try {
        if (process.platform === 'win32') {
            console.log('[INFO] Installing Java on Windows using Chocolatey...');
            const child = spawn('cmd', ['/c', 'choco install jdk -y'], { stdio: 'inherit' });

            child.on('close', (code) => {
                callback(code === 0);
            });
        } else if (process.platform === 'darwin') {
            console.log('[INFO] Installing Java on macOS using Homebrew...');
            const child = spawn('brew', ['install', '--cask', 'temurin'], { stdio: 'inherit' });

            child.on('close', (code) => {
                callback(code === 0);
            });
        } else if (process.platform === 'linux') {
            console.log('[INFO] Installing Java on Linux using apt...');
            const child = spawn('sudo', ['apt-get', 'update'], { stdio: 'inherit' });

            child.on('close', () => {
                const javaInstall = spawn('sudo', ['apt-get', 'install', '-y', 'default-jdk'], { stdio: 'inherit' });

                javaInstall.on('close', (code) => {
                    callback(code === 0);
                });
            });
        } else {
            console.error('[ERROR] Unsupported platform. Please install Java manually.');
            callback(false);
        }
    } catch (error) {
        console.error('[ERROR] Failed to perform Java installation:', error.message);
        callback(false);
    }
}

// Check if a command exists
function checkInstallation(command, args, successCallback, failureCallback) {
    exec(`${command} ${args.join(' ')}`, (error) => {
        if (error) {
            failureCallback();
        } else {
            successCallback();
        }
    });
}

// Install Node.js
function installNode() {
    console.error('[ERROR] Node.js not found. Please install it manually:');
    console.log(' - https://nodejs.org/');
    process.exit(1);
}

// Start Backend
function startBackend() {
    return new Promise((resolve, reject) => {
        console.log('[INFO] Starting backend server...');
        const backendCommand = process.platform === 'win32' ? 'gradlew.bat' : './gradlew';
        const backendArgs = ['bootRun'];

        if (mode === 'production') {
            backendArgs.push('-Pprod');
        }


        backendProcess = spawn(backendCommand, backendArgs, {
            cwd: path.resolve(__dirname, './backend'),
            shell: true,
            stdio: 'inherit',
        });

        backendProcess.on('error', (err) => {
            console.error(`[ERROR] Backend process error: ${err.message}`);
            reject(err);
        });

        backendProcess.on('close', (code) => {
            console.log(`[INFO] Backend process exited with code ${code}`);
            if (code !== 0) {
                reject(new Error(`Backend process failed with exit code ${code}`));
            }

        });

        // Wait until backend is ready
        waitForPort(8080, 60000)
            .then(() => {
                console.log('[INFO] Backend is up and running.');
                resolve();
            })
            .catch((err) => {
                console.error('[ERROR] Backend failed to start within timeout:', err.message);
                backendProcess.kill('SIGINT');
                reject(err);
            });
    });
}

// Start Frontend
function startFrontend() {
    console.log(`[INFO] Starting React app in ${mode} mode...`);
    const frontendPath = path.resolve(__dirname, '.');
    const reactCommand = mode === 'development' ? 'developments' : 'productions';

    frontendProcess = spawn('npm', ['run', reactCommand], {
        cwd: frontendPath,
        shell: true,
        stdio: 'inherit',
    });

    frontendProcess.on('error', (err) => {
        console.error(`[ERROR] Frontend process error: ${err.message}`);
    });

    frontendProcess.on('close', (code) => {
        console.log(`[INFO] Frontend process exited with code ${code}`);
    });
}

// Wait for backend port to be available
function waitForPort(port, timeout) {
    return new Promise((resolve, reject) => {
        const start = Date.now();
        const interval = setInterval(() => {
            exec(`nc -z localhost ${port}`, (error) => {
                if (!error) {
                    clearInterval(interval);
                    resolve();
                } else if (Date.now() - start > timeout) {
                    clearInterval(interval);
                    reject(new Error(`Timeout waiting for port ${port}`));
                }
            });
        }, 20000);
    });
}

// Main function
async function start() {
    await freePorts();

    checkInstallation(
        'java',
        ['-version'],
        async () => {
            console.log('[INFO] Java is installed.');
            checkInstallation(
                'node',
                ['-v'],
                async () => {
                    console.log('[INFO] Node.js is installed.');

                    try {

                        startFrontend();
                        await startBackend();
                    } catch (error) {
                        console.error('[ERROR] Application startup failed:', error.message);
                    }
                },
                installNode
            );
        },
        installJava
    );
}

// Gracefully handle termination signals
process.on('SIGINT', () => {
    console.log('\n[INFO] Shutting down...');
    if (backendProcess) backendProcess.kill('SIGINT');
    if (frontendProcess) frontendProcess.kill('SIGINT');
    process.exit();
});

start().catch((err) => {
    console.error(`[ERROR] Application failed to start: ${err.message}`);
    process.exit(1);
});
