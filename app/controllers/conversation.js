'user strict';
var User = require('../models/user');
var Conversation = require('../models/conversation');
var Message = require('../models/message');
var utils = require('../helper/utils');
var async = require('async');
var FollowInfo = require("../models/followInfo");
var ObjectID = require('mongodb').ObjectID;

var ConversationController = {
  createConversationToUserWithToken: function (req, res, next) {
    // start conversation with user
    if (req.user) {
      return ConversationController.createConversationToUserAsUser(req, res, next);
    } else {
      return ConversationController.createConversationToUserAsAnonymous(req, res, next);
    }
  },

  createConversationToUserAsAnonymous: function (req, res, next) {
    return utils.responses(res, 200, {message: "Chat with anomymous"});
  },

  checkIfConversationExists: function (userA, userB, cb) {
    Conversation.findOne({
      $or:
        [
        {
          createUser: userA,
          targetUser: userB
        },
        {
          createUser: userB,
          targetUser: userA
        }
      ]
    })
    .populate("createUser")
    .populate("targetUser")
    .exec(cb);
  },

  createConversationToUserAsUser: function (req, res, next) {

    var targetUserID = req.body.target_user_id;
    if (!targetUserID) {
        return utils.responses(res, 400, {message: "Missing or target user id is not valid"});
    }

    async.series({
      conversation: function(callback){
        ConversationController.checkIfConversationExists(req.user._id, targetUserID, callback);
      },

      isFollowed: function(callback) {
        FollowInfo.findOne({
          follower: req.user._id,
          followee: ObjectID(targetUserID)
        }).exec(callback)
      }
    },
    function(err, results){

      console.log(results);

      // conversation exists 
      if(results.conversation) {
        var plainConversation = results.conversation.toObject();
        plainConversation.isFollowed = results.isFollowed ? true : false;
        return utils.responses(res, 200, plainConversation);
      }

      User.findOne({_id: targetUserID})
      .exec(function(err, targetUser) {

        if(err) {
          return utils.responses(res, 400, {message: "Missing or target user id is not valid"});
        }

        var conversation = new Conversation();
        conversation.createUser = req.user._id;
        conversation.targetUser = targetUser._id;

        conversation.save(function(err, r) {
          if(err) {
            return utils.responses(res, 500, {message: "Something went wrong"});
          }

          var plainConversation = r.toObject();
          plainConversation.isFollowed = results.isFollowed ? true : false;
          return utils.responses(res, 200, plainConversation);
        })

      })
    });


  },

  getListConversation: function (req, res, next) {
    var userID = req.user._id;
    Conversation.find({
      $or: [
        {
          createUser: userID
        },
        {
          targetUser: userID
        }
      ]
    })
    .populate("createUser")
    .populate("targetUser")
    .exec(function(err, result) {
        if(err) {
          return utils.responses(res, 500, {message: "Something went wrong"});
        }

        var returnArray = result.map(function(item) {
          var plainObject = item.toObject();

          return plainObject;
        });

        async.series(result.map(function(item) {
          return function(callback) {

            Message.findOne({conversation: item._id})
            .populate("user")
            .sort({
              createdAt: -1
            })
            .exec(callback)
          }
        }), function(err, results) {
          var finalResult = returnArray.map(function(item, index) {
            item.lastMessage = results[index];
            return item;
          });

          return utils.responses(res, 200, finalResult);

        });

    });
  }
};

module.exports = ConversationController;
