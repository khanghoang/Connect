var path = require('path');
var webpack = require('webpack');
var bourbon = require('node-bourbon').includePaths;

module.exports = {
  devtool: 'eval',
  entry: [
    './game/scripts/app'
  ],
  output: {
    path: path.join(__dirname, 'game/build'),
    filename: 'bundle.js',
    publicPath: 'build/',
    contentBase: 'game/public/'
  },
  plugins: [
    new webpack.NoErrorsPlugin()
  ],
  resolve: {
    modulesDirectories: ['web_modules', 'node_modules', 'game/scripts', 'game/styles', 'game/images'],
    extensions: ['', '.js', '.jsx']
  },
  module: {
    loaders: [
      {
        test: /\.jsx|js?$/,
        loaders: ['babel?stage=1']
      },
      {
        test: /\.scss$/,
        loader: 'style!css!sass?includePaths[]=' + bourbon
      },
      { test: /\.(png|jpg|gif)$/, loader: 'url-loader?limit=8192'},

      { test: /\.woff(\?v=\d+\.\d+\.\d+)?$/, loader: "url?limit=10000&minetype=application/font-woff" },
      { test: /\.otf$/, loader: "url?limit=10000&minetype=font/opentype" },
      { test: /\.woff2(\?v=\d+\.\d+\.\d+)?$/, loader: "url?limit=10000&minetype=application/font-woff" },
      { test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, loader: "url?limit=10000&minetype=application/octet-stream" },
      { test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, loader: "file" },
      { test: /\.svg(\?v=\d+\.\d+\.\d+)?$/, loader: "url?limit=10000&minetype=image/svg+xml" }
    ]
  }
};
