'use strict';
var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var CreateUpdatedAt = require('mongoose-timestamp');
var User = require('./user');

var Conversation = new Schema({

  createUser: {
    type : Schema.ObjectId,
    ref : 'User'
  },

  targetUser: {
    type : Schema.ObjectId,
    ref : 'User'
  },

  messages: {
    type: Array
  }

});

Conversation.plugin(CreateUpdatedAt);

/**
 * Methods
 */
Conversation.statics.createConversationToUserWithToken = function (token, userID, cb) {
}

module.exports = mongoose.model('Conversation', Conversation);

