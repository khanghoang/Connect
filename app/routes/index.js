var express = require('express');
var Route = express.Router();
var config = require('../config/config');
var passport = require('passport');
var lodash = require('lodash')
var Auth = require(config.root + '/middleware/authorization');
var fs = require('fs');
var utils = require(config.root + '/helper/utils');

var userController = require(config.root + '/controllers/users');
var NotificationController = require(config.root + '/controllers/PushNotificationController');
var trickController = require(config.root + '/controllers/tricks');

var API = {}
API.Users = require(config.root + '/controllers/API/users');

// API Routes
// Route
//   .all('/api/*', Auth.bearerToken)
//   .all('/api/*', function(req, res, next) {
//     res.header("Access-Control-Allow-Origi/", "*");
//     res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
//     res.header("Access-Control-Allow-Headers", "Authorization");
//     if(req.method === "OPTIONS") {
//       res.statusCode = 204;
//       return res.end();
//     } else {
//       return next();
//     }
//   })
//   .all('/api/*', Auth.APIrequiresUserLogin)
//   .get('/api/user/current', API.Users.get_profile)
//   .get('/api/friends', API.Users.get_friends)
//   .post('/login/facebookLogin', Auth.facebookLogin)
//   .post('/login/googlePlusLogin', Auth.googlePlusLogin)

// Frontend routes
Route
  .get('/login', userController.login)
  .get('/signup', userController.signup)
  .get('/logout', userController.logout)
  .get('/forgot-password', userController.getForgotPassword)
  .post('/forgot-password',Auth.hasLogin, userController.postForgotPassword)
  .get('/reset/:token', Auth.hasLogin, userController.getResetPassword)
  .post('/reset/:token', Auth.hasLogin, userController.postResetPassword)
  .post('/users/create', userController.create)
  .get('/dashboard', Auth.requiresLogin, userController.show)
  .post('/users/session',
    passport.authenticate('local', {
    failureRedirect: '/login',
    failureFlash: true
    }), userController.session)
  .get('/auth/twitter', passport.authenticate('twitter'))
  .get('/auth/twitter/callback',
    passport.authenticate('twitter',{
    failureRedirect: '/login' }), function(req, res) {
    res.redirect(req.session.returnTo || '/');
  })
  .get('/auth/facebook', passport.authenticate('facebook', { scope: ['email', 'user_location'] }))
  .get('/auth/facebook/callback', passport.authenticate('facebook', { failureRedirect: '/login' }), function(req, res) {
    res.redirect(req.session.returnTo || '/');
  })
  .get('/', function(req, res) {
    console.log('   >> Worker PID:', process.pid);
    res.render('index', {
      title: 'Express 4'
    });
  })
  .get('/push', function(req, res) {
    // NotificationController.sendNotificationToUserByEmail("lanna.blue89@gmail.com", "Lanna");
    NotificationController.sendNotificationToUserByFacebookID("10206503896498323", "Challenge", {
      gameID: '123123',
      key: "abc"
    });
    res.render('index', {
      title: 'Express 4'
    });
  })

module.exports = Route;
