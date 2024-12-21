const {
    sentryWebpackPlugin
} = require("@sentry/webpack-plugin");

const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");

module.exports = {
    plugins: [new NodePolyfillPlugin(), sentryWebpackPlugin({
        authToken: process.env.SENTRY_AUTH_TOKEN,
        org: "sopotekinc",
        project: "javascript-react"
    })],

    // Other Webpack configurations...
    module: {
        rules: [
            {
                test: /\.js$/,
                enforce: 'pre',
                use: ['source-map-loader'],
                exclude: /node_modules/, // Ignore source maps from node_modules
            },
        ],
    },

    devtool: "source-map"
};