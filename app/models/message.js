'use strict';
var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var CreateUpdatedAt = require('mongoose-timestamp');
var User = require('./user');
var Conversation = require('./conversation');

var Message = new Schema({

  user: {
    type: Schema.ObjectId,
    ref: "User"
  },

  conversation: {
    type: Schema.ObjectId,
    ref: "Conversation"
  },

  content: {
    type: String
  },

});

Message.plugin(CreateUpdatedAt);

/**
 * Methods
 */

module.exports = mongoose.model('Message', Message);

