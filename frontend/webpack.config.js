const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const webpack = require('webpack');

const outputFolder = 'dist';

module.exports = {
    entry: {
        index: './index.js',
        extract: './extract.js'
    },
    output: {
        filename: '[name]-bundle.js',
        path: path.resolve(__dirname, outputFolder)
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                loader: 'babel-loader',
                query: {
                    presets: ['babel-preset-es2015']
                }
            },

            {
                test: /\.css$/,
                use: [
                    {loader: 'style-loader'},
                    {loader: 'css-loader'}
                ]
            },
            { test: /\.(png|woff|woff2|eot|ttf|svg)$/, loader: 'url-loader?limit=100000' }
        ]
    },
    plugins: [
        new CopyWebpackPlugin([
            {from: 'index.html', to: 'index.html'},
            {from: 'extract.html', to: 'extract.html'},
            // this lib is deprecated and has problems with bundling,
            // when loaded normally it crashes on 'this' being undefined
            // for some reason, probably it won't work in strict mode,
            // so this trick is required
            {from: 'node_modules/webcamjs/webcam.min.js', to: 'webcam.min.js'}
        ]),
    ]
};
