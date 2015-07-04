// Module dependencies
var path     = require('path');
var fs       = require('fs');
var express  = require('express');
var mongoose = require('mongoose');
var passport = require('passport');
var config   = require(__dirname + '/app/config/config');
var serveStatic = require('serve-static');
var Auth = require('./app/middleware/authorization');
var User = require('./app/models/user');
var Conversation = require('./app/models/conversation');
var Message = require('./app/models/message');
var PushNotificationController = require('./app/controllers/PushNotificationController');

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

io.use(function(socket, next) {
  if(!socket.request._query) {
      return next(new Error("not authorized"));
  }

  var token = socket.request._query.token;
  Auth.checkAuthToken(app, token, function(err, user) {
    if (err || !user) {
      next(new Error("not authorized"));
    }

    socket.user = user;
    next();
  })
});

io.on('connection', function (socket) {

  console.log("New connection", socket.id);
  // make user online
  var user = socket.user;

  User.update({_id: user._id},
              {
                $set: {
                  online: true
                }
              }).exec();

  socket.on('disconnect', function(){
    console.log(socket.request._query, "is disconnected");

    // make user offline
    User.update({_id: user._id},
                {
                  $set: {
                    online: false
                  }
                }).exec();
  });

  // join room
  socket.on('joinRoom', function(roomID){
    socket.room = roomID;

    Conversation.findOne(socket.room)
    .populate("createUser")
    .populate("targetUser")
    .exec(function (err, conversation) {

      if(err || !conversation) {
        socket.emit('log', 'SERVER', 'room doenst exsit');
      }

      // if user is not createUser or targetUser
      // we deny it
      if(conversation.createUser._id.equals(socket.user._id) ||
         conversation.targetUser._id.equals(socket.user._id)
        )
      {
        socket.join(roomID);
        socket.emit('log', 'SERVER', 'you have connected to room');

        Message.find({conversation: socket.roomID})
        .populate("user")
        .exec(function (err, messages) {
          socket.emit('joinRoomSuccessfully', {messages: messages});
        })

      } else {
        socket.emit('log', 'SERVER', 'you dont have permissions to join this room');
      }

    });
  });

  socket.on('sendChat', function(data) {
    var message = new Message();
    message.content = data;

    Conversation.findOne(socket.room)
    .exec(function (err, conversation) {

      message.conversation = conversation;
      var plainObject = message.toObject();
      plainObject.user = user;

      message.save(function(err, m) {
        if(err) {
          return;
        }

        message.user = user;
        io.sockets.in(socket.room).emit('updateChat', plainObject);
      })

      if(!conversation.createUser.online) {
        //TODO: push notificaiton
        PushNotificationController.sendNotificationToUserByUserID(createUser._id, message.content);
      }

      if(!conversation.targetUser.online) {
        //TODO: push notificaiton
        PushNotificationController.sendNotificationToUserByUserID(targetUser._id, message.content);
      }

    });


  });

});
