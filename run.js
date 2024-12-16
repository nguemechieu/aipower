const { spawn } = require('child_process');
const path = require('path');
const { exec } = require('child_process');

const mode = process.argv[2] || 'development';
console.log(`Starting application in ${mode} mode...\n`);

const ports = [8080, 3000];
let backendProcess, frontendProcess;

// Function to kill processes running on a specific port
function killProcess(port) {
    return new Promise((resolve) => {
        exec(`lsof -i :${port} | grep LISTEN`, (err, stdout) => {
            if (err || !stdout.trim()) {
                console.log(`Port ${port} is free.`);
                resolve();
                return;
            }

            // Extract and kill PIDs
            const pids = stdout
                .split('\n')
                .map((line) => line.split(/\s+/)[1])
                .filter(Boolean);

            const killPromises = pids.map((pid) =>
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
        });
    });
}

// Kill processes on all ports
async function freePorts() {
    console.log('Checking and freeing ports...');
    await Promise.all(ports.map(killProcess));
    console.log('Ports are now free.');
}

// Function to check for installation
function checkInstallation(command, args, onSuccess, onFailure) {
    const checkProcess = spawn(command, args, { shell: true });

    checkProcess.on('error', () => onFailure());
    checkProcess.on('close', (code) => (code === 0 ? onSuccess() : onFailure()));
}

// Install Java
function installJava() {
    console.log('Java not found. Please install manually:');
    console.log('Oracle: https://www.oracle.com/java/');
    console.log('AdoptOpenJDK: https://adoptopenjdk.net/');
    process.exit(1);
}

// Install Node.js
function installNode() {
    console.log('Node.js not found. Please install manually:');
    console.log('https://nodejs.org/');
    process.exit(1);
}

// Start Backend
function startBackend() {
    console.log('Starting backend server...');
    const backendCommand = process.platform === 'win32' ? 'gradlew.bat' : './gradlew';
    const backendArgs = ['bootRun'];

    if (mode === 'production') {
        backendArgs.push('-Pprod');
    }

    backendProcess = spawn(backendCommand, backendArgs, {
        cwd: path.join(__dirname, '.'),
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

// Start Frontend
function startFrontend() {
    const frontendPath = path.join(__dirname, 'frontend');
    const command = mode === 'production' ? 'production' : 'development';

    console.log(`Starting React app in ${mode} mode...`);
    frontendProcess = spawn('npm', ['run', command], {
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
            console.log('Java is installed.');

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
}

// Gracefully handle termination signals
process.on('SIGINT', () => {
    console.log('\nShutting down...');
    if (backendProcess) backendProcess.kill('SIGINT');
    if (frontendProcess) frontendProcess.kill('SIGINT');
    process.exit();
});

process.on('SIGTERM', () => {
    console.log('\nShutting down due to SIGTERM...');
    if (backendProcess) backendProcess.kill('SIGTERM');
    if (frontendProcess) frontendProcess.kill('SIGTERM');
    process.exit();
});

start();
