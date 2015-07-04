'user strict';
var User = require('../models/user');
var Conversation = require('../models/conversation');
var utils = require('../helper/utils');
var async = require('async');

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
    }).exec(cb);
  },

  createConversationToUserAsUser: function (req, res, next) {

    var targetUserID = req.body.target_user_id;
    if (!targetUserID) {
        return utils.responses(res, 400, {message: "Missing or target user id is not valid"});
    }

    async.series({
      conversation: function(callback){
        ConversationController.checkIfConversationExists(req.user._id, targetUserID, callback);
      }
    },
    function(err, results){

      console.log(results);

      // conversation exists 
      if(results.conversation) {
          return utils.responses(res, 200, results.conversation);
      }

      User.findOne({_id: targetUserID})
      .exec(function(err, targetUser) {

        if(err) {
          return utils.responses(res, 400, {message: "Missing or target user id is not valid"});
        }

        var conversation = new Conversation();
        conversation.createUser = req.user._id;
        conversation.targetUser = targetUser._id;

        conversation.save(function(err, result) {
          if(err) {
            return utils.responses(res, 500, {message: "Something went wrong"});
          }

          return utils.responses(res, 200, result);
        })

      })
    });


  },

  getListConversation: function (req, res, next) {
    var userID = req.user._id;
    Conversation.find({
      createUser: userID
    }).or({
      targetUser: userID
    }).exec(function(err, result) {
        if(err) {
          return utils.responses(res, 500, {message: "Something went wrong"});
        }
        return utils.responses(res, 200, result);
    });
  }
};

module.exports = ConversationController;
