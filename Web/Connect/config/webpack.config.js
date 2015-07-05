var path = require('path');
var webpack = require('webpack');
var bourbon = require('node-bourbon').includePaths;
var merge = require('lodash/object/merge');
var minimist = require('minimist');
var ExtractTextPlugin = require('extract-text-webpack-plugin');

var argv = minimist(process.argv.slice(2));
var DEBUG = !argv.release;
var NODE_MODULES_DIR = path.resolve(__dirname, 'node_modules');
var STYLE_LOADER = 'style-loader';
var CSS_LOADER = `css-loader!sass-loader?sourceMap&includePaths[]=${bourbon}&includePaths[]=${path.resolve(__dirname, '../node_modules/bootstrap-sass/assets/stylesheets')}`;

if (!DEBUG) {
    CSS_LOADER = `css-loader?minimize!sass-loader?sourceMap&includePaths[]=${bourbon}&includePaths[]=${path.resolve(__dirname, '../node_modules/bootstrap-sass/assets/stylesheets')}`;
}

var deps = [
    '../react/dist/react.min.js',
    '../react-router/dist/react-router.min.js'
];

var config = {
    output: {
        publicPath: './',
        sourcePrefix: '  '
    },
    resolve: {
        alias: {}
    },
    plugins: [],
    module: {
        noParse: [],

        loaders: [{
            test: /\.js?$/,
            exclude: [NODE_MODULES_DIR],
            loader: 'babel'
        },
        {
            test: /\.scss$/,
            loader: `${STYLE_LOADER}!${CSS_LOADER}`
        },
        { test: /\.(png|jpg|gif)$/, loader: 'url-loader?limit=8192'},

        // **IMPORTANT** This is needed so that each bootstrap js file required by
        // bootstrap-webpack has access to the jQuery object
        { test: /bootstrap-sass\/assets\/javascripts\//, loader: 'imports?jQuery=jquery' },

        // Needed for the css-loader when [bootstrap-webpack](https://github.com/bline/bootstrap-webpack)
        // loads bootstrap's css.
        { test: /\.woff(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&minetype=application/font-woff' },
        { test: /\.woff2(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&minetype=application/font-woff' },
        { test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&minetype=application/octet-stream' },
        { test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, loader: 'file' },
        { test: /\.otf(\?v=\d+\.\d+\.\d+)?$/, loader: 'file' },
        { test: /\.svg(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&minetype=image/svg+xml' }
        ]
    }
};

// Run through deps and extract the first part of the path,
// as that is what you use to require the actual node modules
// in your code. Then use the complete path to point to the correct
// file and make sure webpack does not try to parse it
deps.forEach(function(dep) {
    var depPath = path.resolve(NODE_MODULES_DIR, dep);
    config.resolve.alias[dep.split(path.sep)[0]] = depPath;
    config.module.noParse.push(depPath);
});

//
// Configuration for the client-side bundle (app.js)
// -----------------------------------------------------------------------------

const appConfig = merge({}, config, {
    entry: [
        './src/app.js'
    ],
    output: {
        path: './build/public',
        filename: 'app.js'
    },
    devtool: DEBUG ? 'eval' : false,
    plugins: config.plugins.concat([
        new webpack.DefinePlugin({
            'process.env': {
                BROWSER: JSON.stringify(true)
            }
        })
    ].concat(DEBUG ? [] : [
        new webpack.optimize.DedupePlugin(),
        new webpack.optimize.UglifyJsPlugin(),
        new webpack.optimize.AggressiveMergingPlugin()
    ]))
});

//
// Configuration for the server-side bundle (server.js)
// -----------------------------------------------------------------------------

const serverConfig = merge({}, config, {
    entry: './src/server.js',
    output: {
        path: './build',
        filename: 'server.js',
        libraryTarget: 'commonjs2'
    },
    target: 'node',
    externals: /^[a-z][a-z\.\-0-9]*$/,
    node: {
        console: false,
        global: false,
        process: false,
        Buffer: false,
        __filename: false,
        __dirname: false
    },
    devtool: DEBUG ? 'source-map' : 'cheap-module-source-map',
    plugins: config.plugins.concat([
        new webpack.DefinePlugin({
            'process.env': {
                BROWSER: JSON.stringify(true)
            }
        }),

        new ExtractTextPlugin('public/styles.css')
    ]),
    module: {
        loaders: config.module.loaders.map(function(loader) {
            if (loader.loader.indexOf(STYLE_LOADER) > -1) {
                loader.loader = ExtractTextPlugin.extract('style-loader', `${CSS_LOADER}`);
            }

            return loader;
        })
    }
});

module.exports = [appConfig, serverConfig];
