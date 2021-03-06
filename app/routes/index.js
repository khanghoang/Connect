var express = require('express');
var Route = express.Router();
var config = require('../config/config');
var passport = require('passport');
var lodash = require('lodash')
var Auth = require(config.root + '/middleware/authorization');
var fs = require('fs');
var utils = require(config.root + '/helper/utils');

var Auth = require(config.root + '/middleware/authorization');
var userController = require(config.root + '/controllers/users');
var NotificationController = require(config.root + '/controllers/PushNotificationController');
var trickController = require(config.root + '/controllers/tricks');
var ConversationModel = require(config.root + '/models/conversation');
var ConversationController = require(config.root + '/controllers/conversation');
var FollowController = require(config.root + '/controllers/FollowController');

var API = {}
API.Users = require(config.root + '/controllers/API/users');

Route
  .all('/api/*', Auth.bearerToken)
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
  .post('/login/facebookLogin', Auth.facebookLogin)
  .post('/api/conversation/create', ConversationController.createConversationToUserWithToken)
  .get('/api/conversation/list', ConversationController.getListConversation)

  .get('/auth/facebook', passport.authenticate('facebook', { scope: ['email', 'user_location'] }))
  .get('/auth/facebook/callback', passport.authenticate('facebook', { failureRedirect: '/login' }), function(req, res) {
    res.redirect(req.session.returnTo || '/');
  })

  .get('/', function(req, res) {
    res.render('index', {
      _csrf: res.locals._csrf,
      title: 'Express 4'
    });
  })

  .get('/boardcast', Auth.requiresLogin, function(req, res) {
    res.render('boardcast', {
      _csrf: res.locals._csrf,
      title: 'Boardcast'
    });

    console.log("request user", req.user);
  })

  .post('/boardcast', function(req, res, next) {
    console.log("debug");
    console.log("message = ", req.body.message);
    NotificationController.boardcastMessageByUser(req.user._id.toString(), req.body.message);
    req.flash('success', { msg: 'Send successfully' });
    res.redirect("/boardcast");
  })

  .post('/api/user/follow', FollowController.followUserByUserID)
  .post('/api/user/unfollow', FollowController.unfollowUserByUserID)

module.exports = Route;
