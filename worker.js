// Module dependencies
var path     = require('path');
var fs       = require('fs');
var express  = require('express');
var mongoose = require('mongoose');
var passport = require('passport');
var config   = require(__dirname + '/app/config/config');
var serveStatic = require('serve-static');

module.exports.run = function (worker) {
  console.log('   >> Worker PID:', process.pid);
  var app = require('express')();

  var httpServer = worker.httpServer;
  var scServer = worker.scServer;

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
  app.listen(app.get('port'), function() {
    console.log("\n✔ Express server listening on port %d in %s mode", app.get('port'), app.get('env'));
  });

  app.use(serveStatic(path.resolve(__dirname, 'public')));

  httpServer.on('request', app);

  var count = 0;

  /*
    In here we handle our incoming realtime connections and listen for events.
  */
  scServer.on('connection', function (socket) {

    socket.on('subscribe', function(data) {
      socket.on(data, function(mess) {
        console.log(data, mess);
      })
    });

    socket.on('login', function (credentials, respond) {
      var userID = credentials.userID;

      socket.setAuthToken({
        userID: userID
      });

      respond();
    });

    socket.on('disconnect', function () {
      var authToken = socket.getAuthToken();
      if (authToken.userID && authToken.gameID) {
        var userID = authToken.userID;
        var gameID = authToken.gameID;
        console.log(userID, 'disconnected');
        // find all the game that use in
        // check user is played = true
        // remove the game

        Game.update({
          _id: gameID,
          'userGame.isEnded': false
        }, {
          $set: {
            isCanceled: true
          }
        }).exec(function (err, numberOfEffected) {
          if (numberOfEffected) {
            Challenge.findOneAndUpdate({
              game: gameID
            }, {
              isPlayed: true
            }).exec();

            console.log(userID, 'has disconnect to the game => cancle the game');
          }

        });

      }
    })

    socket.on("raw", function (obj, opt) {
      // obj is a string
      obj = JSON.parse(obj);

      if (!obj.action) {
        return;
      }

      var action = obj.action;
      var data = obj.data;

      switch (action) {
        case "CREATE_GAME":
          gameManager.createGame(socket, data);
          break;
        case "PLAY_GAME":
          gameManager.playingGameOfflineMode(socket, scServer, data.gameID, data.gameID + ':' + data.userID);
          break;
        case "PLAY_GAME_SINGLE_MODE":
          gameManager.playingGameSingleMode(socket, scServer, data.gameID, data.gameID);
          break;
        case "PLAY_GAME_ALONE": {
          gameManager.playingGameOfflineMode(socket, scServer, data.gameID, data.gameID + ':' + data.userID);
          Challenge.saveChallengeWithGameID(data.gameID, function (err, result) {
            if (err) {
              console.log(err);
              return;
            }
          });
          break;
        }
        case "DESTROY_GAME":
          gameManager.destroyGame(socket, data.gameID);
          break;
        case "SUBMIT_ANSWER":
          gameManager.submitAnswer(socket, data);
          break;
      }
    });
  });

  scServer.addMiddleware(scServer.MIDDLEWARE_PUBLISH_IN, function(socket, channel, data, next) {
    Game.findOne({_id: channel})
    .exec(function(err, game) {
      console.log(err, game);
      if(game && game.numberOfCurrentConnections < 2) {
        next();
        return;
      }

      next(socket.id + " is blocked from PUBLISH");
      return;
    });

  });

  scServer.addMiddleware(scServer.MIDDLEWARE_SUBSCRIBE, function(socket, channel, next) {
    var authToken = socket.getAuthToken();
    var gameID = channel.split(':')[0];
    if (!authToken.userID) {
      return next(socket.id + " is blocked, unauthorized");
    }

    Game.findOne({_id: gameID})
    .exec(function(err, game) {
      console.log('subscribe ======== ', err, game);
      if (game.isCanceled) {
        return next({
          type: 'GAME_CANCELED',
          message: 'Game canceled'
        });
      }

      if (game && game.numberOfCurrentConnections < 2) {
        // block user
        if (game.isOfflineMode && !game.userGame.isEnded) {
          return next({
            type: 'GAME_ALREADY_STARTED',
            message: 'Quiz has already started, please check the challenges screen after 1 min.'
          });
        }

        // if single mode then don't increase it
        if (channel.indexOf(':') === -1) {
          game.numberOfCurrentConnections = game.numberOfCurrentConnections + 1;
        }

        if (game.numberOfCurrentConnections === 1) {
          if (game.userGame.isPlay) {
            next(socket.id + " has played this game");
          }

        } else {
          // the second user play offline
          // if (!game.friendGame.isPlay && authToken.userID === game.friend) {
          //   game.friendGame.isPlay = true;
          //   game.markModified("friendGame");
          //   game.save();

          //   return console.log('friend join game');
          // }
          game.friendGame.isPlay = true;
        }

        game.markModified("friendGame");
        game.markModified("userGame");

        game.save(function () {
          if (game.numberOfCurrentConnections === 2) {
            gameManager.playingGame(socket, scServer, gameID, channel);
          }

          return next();
        });
        return;
      }

      next(socket.id + " is blocked from SUBSCRIBE");
      return;
    });

  });

};
