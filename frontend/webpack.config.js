const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");

module.exports = {plugins: [
        new NodePolyfillPlugin()
    ],
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
};
