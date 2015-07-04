'user strict';
var User = require('../models/user');
var Conversation = require('../models/conversation');
var utils = require('../helper/utils');

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
  createConversationToUserAsUser: function (req, res, next) {

    var targetUserID = req.body.target_user_id;
    User.find(targetUserID)
    .exec(function(err, targetUser) {

      if(err) {
        return utils.responses(res, 400, {message: "Missing or target user id is not valid"});
      }

      var conversation = new Conversation({});
      conversation.currentUser = req.user;
      conversation.targetUser = targetUser;

      conversation.save(function(err, result) {
        return utils.responses(res, 200, result);
      })

    })

  }
};

module.exports = ConversationController;
