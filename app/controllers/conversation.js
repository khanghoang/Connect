'user strict';

var Conversation = {
  createConversationToUserWithToken: function (req, res, next) {
    // start conversation with user
    if (req.user) {
      return this.createConversationToUserAsUser(req, res, next);
    }

    return this.createConversationToUserAsAnnonymous(req, res, next);
  },

  createConversationToUserAsAnonymous: function (req, res, next) {

  },
  createConversationToUserAsUser: function (req, res, next) {

  }
};

module.exports = Conversation;
