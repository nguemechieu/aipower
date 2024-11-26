module.exports = {
    transform: {
        "^.+\\.(js|jsx|ts|tsx)$": "babel-jest" // Use Babel for transforming JavaScript
    },
    transformIgnorePatterns: [
        "/node_modules/(?!axios)" // Include `axios` for transformation
    ],
    testEnvironment: "jsdom" // Required for React testing
};
