// Module dependencies
var path     = require('path');
var fs       = require('fs');
var express  = require('express');
var mongoose = require('mongoose');
var passport = require('passport');
var config   = require(__dirname + '/app/config/config');
var serveStatic = require('serve-static');

var app = require('express')();

app.use(function(req, res, next) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Headers', '*');
  next();
});

var server = require('http').Server(app);
var io = require('socket.io')(server);

if(process.env.NODE_ENV === 'production') {
  app.enable('trust proxy');
  app.use(require('express-enforces-ssl')());
}

app.config = config;

// Database
require('./app/config/database')(app, mongoose);

var models_path = __dirname + '/app/models'

fs.readdirSync(models_path).forEach(function (file) {
  if (~file.indexOf('.js')) 
    require(models_path + '/' + file)
});

require('./app/config/passport')(app, passport);

// express settings
require('./app/config/express')(app, express, passport);

// create a server instance
// passing in express app as a request event handler

server.listen(app.get('port'), function() {
  console.log("\n✔ Express server listening on port %d in %s mode", app.get('port'), app.get('env'));
});

io.on('connection', function (socket) {

  socket.on('disconnect', function(){
    console.log(socket, "is disconnected");
  });

  console.log("New connection", socket);
  socket.on('my other event', function (data) {
    console.log(data);
  });
});
