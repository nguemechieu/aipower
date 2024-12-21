const { spawn, exec } = require('child_process');
const path = require('path');

const mode = process.argv[2] || 'development';
console.log(`[INFO] Starting application in ${mode} mode...\n`);

const ports = [8080, 3000];
let backendProcess, frontendProcess;

// Function to kill processes running on a specific port
function killProcess(port) {
    return new Promise((resolve, reject) => {
        exec(`lsof -i :${port} | grep LISTEN`, (err, stdout) => {
            if (err || !stdout.trim()) {
                console.log(`[INFO] Port ${port} is free.`);
                resolve();
                return;
            }

            const pids = stdout
                .split('\n')
                .map((line) => line.split(/\s+/)[1])
                .filter(Boolean);

            const killPromises = pids.map((pid) =>
                new Promise((killResolve) => {
                    exec(`kill -9 ${pid}`, (killErr) => {
                        if (killErr) {
                            console.error(`[ERROR] Failed to kill process ${pid} on port ${port}: ${killErr.message}`);
                        } else {
                            console.log(`[INFO] Successfully killed process ${pid} on port ${port}`);
                        }
                        killResolve();
                    });
                })
            );

            Promise.all(killPromises)
                .then(() => resolve())
                .catch((killErr) => reject(killErr));
        });
    });
}

// Free all required ports
async function freePorts() {
    console.log('[INFO] Checking and freeing ports...');
    await Promise.all(ports.map(killProcess));
    console.log('[INFO] Ports are now free.');
}

// Function to check installations
function checkInstallation(command, args, onSuccess, onFailure) {
    const checkProcess = spawn(command, args, { shell: true });

    checkProcess.on('error', () => onFailure());
    checkProcess.on('close', (code) => (code === 0 ? onSuccess() : onFailure()));
}

// Install Java
function installJava() {
    console.error('[ERROR] Java not found. Please install it manually:');
    console.log(' - Oracle: https://www.oracle.com/java/');
    console.log(' - AdoptOpenJDK: https://adoptopenjdk.net/');
    process.exit(1);
}

// Install Node.js
function installNode() {
    console.error('[ERROR] Node.js not found. Please install it manually:');
    console.log(' - https://nodejs.org/');
    process.exit(1);
}

// Start Backend
function startBackend() {
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
    });

    backendProcess.on('close', (code) => {
        console.log(`[INFO] Backend process exited with code ${code}`);
    });
}

// Start Frontend
function startFrontend() {
    console.log(`[INFO] Starting React app in ${mode} mode...`);
    const frontendPath = path.resolve(__dirname, './frontend');
    const reactCommand = mode === 'production' ? 'build' : 'start';

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

    // Start backend regardless of mode
    startBackend();
}

// Main function
async function start() {
    await freePorts();

    checkInstallation(
        'java',
        ['-version'],
        () => {
            console.log('[INFO] Java is installed.');

            checkInstallation(
                'node',
                ['-v'],
                () => {
                    console.log('[INFO] Node.js is installed.');
                    startFrontend();
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

process.on('SIGTERM', () => {
    console.log('\n[INFO] Shutting down due to SIGTERM...');
    if (backendProcess) backendProcess.kill('SIGTERM');
    if (frontendProcess) frontendProcess.kill('SIGTERM');
    process.exit();
});

start();
