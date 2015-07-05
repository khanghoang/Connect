'use stricts'
GLOBAL.PARSE = require('parse').Parse;
var _ = require("lodash");
var async = require('async');
var FollowInfo = require("../models/followInfo");

exports.initialize = function() {
  var PARSE_ID = process.env.PARSE_ID || "sDe87NsGCpLHg1yv2iAF7RKxYa1mXrN8oVqMhVUG";
  var PARSE_KEY = process.env.PARSE_KEY || "MA0Lo82rbjsS9WiDC7tlnKA8nEEQ6Yoaoe7aUKIt";
  var PARSE_MASTER = process.env.PARSE_MASTER || "w4llNaFGKxlh1hb6gjrJpARBmeMP5zS4htaoDBR2";

  PARSE.initialize(
    PARSE_ID, 
    PARSE_KEY,
    PARSE_MASTER
  );
}

function push(query, data, callback) {

  PARSE.Push.send({
    where: query,
    data: data
  }, {
    success: function () {
      // console.log("arguments", arguments);
      callback(arguments);
    },
    error: function (error) {
      // console.log("Error: " + error.code + " " + error.message);
      callback(null, error);
    }
  });

}

exports.sendNotificationToUserByFacebookID = function(facebookID, message, embedObject) {
  var query = new PARSE.Query(PARSE.Installation);
  var data = {
    "alert": message,
    "badge": 1,
    "sound": "default"
  };

  data = _.assign(data, embedObject);

  query.equalTo("fb_id", facebookID);

  push(query, data, function(success, error) {
    if(error) {
      console.log("Error: " + error.code + " " + error.message);
      return;
    }
  });
}

exports.sendNotificationToUserByEmail = function(email, message, embedObject) {
  var query = new PARSE.Query(PARSE.Installation);
  var data = {
    "alert": message,
    "badge": 1,
    "sound": "default"
  };

  data = _.assign(data, embedObject);

  query.equalTo("user_email", email);

  push(query, data, function(success, error) {
    if(error) {
      console.log("Error: " + error.code + " " + error.message);
      return;
    }
  });
}

function sendNotificationToUserByUserID(userID, message, embedObject) {
  var query = new PARSE.Query(PARSE.Installation);
  var data = {
    "alert": message,
    "badge": 1,
    "sound": "default"
  };

  data = _.assign(data, embedObject);

  query.equalTo("user_id", userID);

  push(query, data, function(success, error) {
    if(error) {
      console.log("Error: " + error.code + " " + error.message);
      return;
    }
  });
}

exports.sendNotificationToUserByUserID = sendNotificationToUserByUserID;

exports.boardcastMessageByUser = function(userID, message, embedObject) {
  var query = new PARSE.Query(PARSE.Installation);
  var data = {
    "alert": message,
    "badge": 1,
    "sound": "default"
  };

  data = _.assign(data, embedObject);

  FollowInfo.find({
    followee: userID
  })
  .populate("follower")
  .exec(function(err, infos) {
    _.each(infos, function(info) {
      sendNotificationToUserByUserID(info.follower._id.toString(), message);
    })
  })
}

exports.sendPushNotificationToAllDevices = function(message, embedObject) {

  var query = new PARSE.Query(PARSE.Installation);
  var data = {
    "alert": message,
    "badge": 1,
    "sound": "default"
  };

  push(query, data, function(success, error) {
    if(error) {
      console.log("Error: " + error.code + " " + error.message);
      return;
    }
  });
}
