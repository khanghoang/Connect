var argv = require('minimist')(process.argv.slice(2));
var SocketCluster = require('socketcluster').SocketCluster;

var socketCluster = new SocketCluster({
  workers: Number(process.env.WORKERS) || Number(argv.w) || 1,
  stores: Number(argv.s) || 1,
  port: process.env.PORT || 3001,
  appName: argv.n || null,
  workerController: __dirname + '/worker.js',
  // storeController: __dirname + '/store.js',
  socketChannelLimit: Number(process.env.SOCKET_CHANNEL_LIMIT) || 100,
  rebootWorkerOnCrash: argv['auto-reboot'] != false
});

