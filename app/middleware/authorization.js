var config = require('../config/config');
var superagent = require('superagent');
var User = require('../models/user');
var async = require('async');
var crypto = require('crypto');
var AccessToken = require('../oauth/models').OAuthAccessTokensModel;
var moment = require('moment');

/*
 *  Generic require login routing middleware
 */

var _ = require('lodash')
var oauth = require('../oauth/models');

var passport = require('passport');

exports.bearerToken = function (req, res, next) {

  if(~req.originalUrl.indexOf("/api/getSplashScreen")) {
    return next();
  }

  passport.authenticate(
    'bearer',
    function(err, user, info)
    {
      if ((err) || (!user))
        {
           var errPrint     = {}
           errPrint.status  = 403
           errPrint.message = (err && err.message) || "Unauthorized, need user session to access this route"

           return res.json(errPrint);
        }

        delete req.query.access_token;
        req.user = user;
        return next();
    }
  )(req, res);
};

// TODO: refactor
exports.googlePlusLogin = function (req, res, next) {

  var request = "https://www.googleapis.com/oauth2/v2/userinfo"
  superagent
  .get(request)
  .set("Authorization", "Bearer " + req.body.access_token)
  .end(function(err, response) {
    var dataJSON = JSON.parse(response.text)

    // console.log(dataJSON);
    if(!dataJSON.email) {
      return res.json(400, {
        code: 400,
        message: "Error when getting data from Google plus, please check google plus access token",
      });
    }

    var user;
    var email = dataJSON.email;

    async.waterfall([
      function(callback) {
        User.findOne({email: email}, function(err, aUser) {
          if(err) {
            // 400
            return res.json(500, {
              code: 500,
              message: "Internal server error when getting user",
              error: err
            });
          }

          user = aUser;
          callback(null, aUser);
        });
      },
      function(aUser, callback) {
        console.log(aUser);
        if(!aUser) {
          user = new User({email: dataJSON.email});
          user.avatar = dataJSON.picture;
          user.name = dataJSON.name;
          user.provider = "token google plus";
          user.tokens = [{
            accessToken: req.body.access_token,
            refreshToken: req.body.refresh_token,
            kind: "google",
          }];
          user.save(function(err, savedUser) {
            console.log(err);
            console.log(savedUser);
            if(err) {
              callback(null);
              return res.json(400, {
                code: 400,
                message: "Internal server error when saving user",
                error: err
              });
            }

            return callback(null);
          });
        } else {
          aUser.tokens = [{
            accessToken: req.body.access_token,
            refreshToken: req.body.refresh_token,
            kind: "google",
          }];

          aUser.save(function () {
            callback(null);
          });
        }
      },

      // genToken
      function(callback) {
        generateRandomToken(function (agr1, token){
          callback(null, false, token);
        })
      },

      // save token
      function(arg1, token, callback) {
        console.log(user);
        oauth.saveAccessToken(token, "DiscoveryClientID", 3600000, user._id, function(err, result) {
          callback(err, result);
        });
      },
    ], function(err, result) {
      console.log(err, result);
      console.log(user);
      if(err) {
        return res.json(500, "Server error");
      }

      return res.json(200, {
        user: user,
        token: result.accessToken
      });
    });

  });

};

exports.checkAuthToken = function (app, accessToken, done) {
  AccessToken.findOne({accessToken:accessToken}, function(err, token) {
    console.log("token" + token);
    if (err) { return done(err); }
    if (!token) { return done(null, false); }

    var now = moment().unix();
    var creationDate = moment(token.createdAt).unix();

    if(app.config.expiredTime) {
      if( now - creationDate > app.config.expiredTime ) {
        console.log('Token expired');
        AccessToken.remove({ token: accessToken }, function (err) {
          if (err) return done(err);
        });
        return done(new Error("Token expired"), false, { message: 'Token expired' });
      }
    }

    var info = {scope: '*'}
    User.findOne({
      id: token.userId
    })
    .exec(function (err, user) {
      User.findOne({
        _id: token.userId
      }, function(err, user) {
        done(err,user,info)
      });
    });
  });
}

// TODO: refactor
exports.facebookLogin = function (req, res, next) {
  var fbAccessToken = req.body.access_token;
  var request = "https://graph.facebook.com/v2.3/me?fields=id,name,email&access_token=" + req.body.access_token;
  superagent
  .get(request)
  .end(function(err, response) {
    var dataJSON = JSON.parse(response.text)

    // console.log(dataJSON);
    if(dataJSON.error && dataJSON.error.code === 190) {
      return res.json(400, {
        code: 400,
        message: "Error when getting data from Facebook, double check the FB access token",
        details: dataJSON.error && dataJSON.error.message
      });
    }

    var user;

    async.waterfall([
      function(callback) {
        User.findOne({email: dataJSON.email}, function(err, aUser) {
          if(err) {
            // 400
            return res.json(500, {
              code: 500,
              message: "Internal server error when getting user",
              error: err
            });
          }

          user = aUser;
          // console.log(user);
          // console.log(err);
          callback(null, aUser);
        });
      },

      function(aUser, callback) {
        // console.log(aUser);
        if(!aUser) {
          user = new User({email: dataJSON.email});
          user.name = dataJSON.name;
          user.provider = "token facebook";
          user.avatar = "https://graph.facebook.com/" + dataJSON.id + "/picture?type=normal",
          user.save(function(err, savedUser) {
            // console.log(err);
            // console.log(savedUser);
            if(err) {
              callback(null);
              return res.json(400, {
                code: 400,
                message: "Internal server error when saving user",
                error: err
              });
            }

            return callback(null, savedUser);
          });
        } else {
          return callback(null, aUser);
        }
      },

      function SaveFacebookAccessToken(user, callback) {
        user.tokens = [{
          accessToken: fbAccessToken,
          kind: "facebook"
        }];

        user.facebookID = dataJSON.id;

        user.save(function(err, result) {
          if (err) {
            return res.json(400, {
              code: 400,
              message: "Internal server error when saving user FB access token",
              error: err
            });
          }

          callback(null);
        })
      },

      // genToken
      function(callback) {
        generateRandomToken(function (agr1, token){
          callback(null, false, token);
        })
      },

      // save token
      function(arg1, token, callback) {
        oauth.saveAccessToken(token, "DiscoveryClientID", 3600000, user._id, function(err, result) {
          callback(result, err);
        });
      }
    ], function(result, err) {
      // console.log(err, result);
      console.log("error " + err);
      // console.log(user);
      if(err) {
        return res.json(500, "Server error");
      }

      return res.json(200, {
        user: user,
        token: result.accessToken
      });
    });
  });
}

/**
 * Internal random token generator
 *
 * @param  {Function} callback
 */
function generateRandomToken (callback) {
  crypto.randomBytes(256, function (ex, buffer) {
    if (ex) return callback(error('server_error'));

    var token = crypto
      .createHash('sha1')
      .update(buffer)
      .digest('hex');

    callback(false, token);
  });
};

exports.requiresLogin = function (req, res, next) {
  if (req.isAuthenticated()) return next()
  if (req.method == 'GET') req.session.returnTo = req.originalUrl
  res.redirect('/login')
}

exports.hasLogin = function (req, res, next) {
  if (req.isAuthenticated()) {
    res.redirect('/')
  } else {
    next()
  }
}

/*
 *  User authorization routing middleware
 */

exports.user = {
  hasAuthorization: function (req, res, next) {
    if (req.profile.id != req.user.id) {
      req.flash('info', 'You are not authorized')
      return res.redirect('/users/' + req.profile.id)
    }
    next()
  }
}

/*
 *  authorization routing middleware if user has login
 */
exports.APIrequiresUserLogin = function (req, res, next) {

  var is_login = req.headers['is_login']

  if( req.isAuthenticated() || is_login ) {
    return next()
  } else {
    var errPrint     = {}
    errPrint.status  = 403
    errPrint.message = "Unauthorized, need user session to access this route"

    return res.json(200, errPrint)
  }
}
