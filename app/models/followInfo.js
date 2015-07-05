'use strict';
var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var CreateUpdatedAt = require('mongoose-timestamp');
var User = require('./user');

var followInfo = new Schema({

  follower: {
    type : Schema.ObjectId,
    ref : 'User'
  },

  followee: {
    type : Schema.ObjectId,
    ref : 'User'
  },

});

followInfo.plugin(CreateUpdatedAt);

/**
 * Methods
 */

module.exports = mongoose.model('FollowInfo', followInfo);

