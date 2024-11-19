module.exports = {
    testEnvironment: "jsdom",
    transform: {
        "^.+\\.jsx?$": "babel-jest",
    },
    transformIgnorePatterns: [
        "/node_modules/(?!axios).+\\.js$", // Ensure axios is included for transformation
    ],
};
