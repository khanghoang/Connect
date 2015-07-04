'use trict';
var User = require('../models/user');
var Conversation = require('../models/conversation');
var utils = require('../helper/utils');
var async = require('async');
var FollowInfo = require("../models/followInfo");

exports.followUserByUserID = function (req, res, next) {
  var targetUserID = req.body.user_id;

  if(!targetUserID) {
    return utils.responses(res, 400, {message: "Missing or target user id is not valid"});
  }

  async.waterfall([
    function(callback) {
      FollowInfo.findOne({
        follower: req.user._id,
        followee: user_id
      }).exec(function(err, followInfo) {

        callback(err, followInfo)
      })
      callback(null, 'one', 'two');
    }
  ], function (err, exists) {
    if(err) {
      return utils.responses(res, 500, {message: "Something went wrong"});
    }

    // if exists alr
    if(exists) {
        return utils.responses(res, 200, followInfo.toObject());
    }

    // if not, then create a new one
    User.findOne(targetUserID)
    .exec(function (err, followee) {

      if(err || !followee) {
        return utils.responses(res, 400, {message: err});
      }

      var followInfo = new FollowInfo();
      followInfo.follower = req.user;
      followInfo.followee = followee;

      followInfo.save(function(err, result) {
        if(err) {
          return utils.responses(res, 500, {message: "Something went wrong"});
        }
        return utils.responses(res, 200, followInfo.toObject());
      });

    })
  });


}
