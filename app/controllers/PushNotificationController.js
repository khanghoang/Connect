'use stricts'
GLOBAL.PARSE = require('parse').Parse;
var _ = require("lodash");

exports.initialize = function() {
  var PARSE_ID = process.env.PARSE_ID || "eV7G9eTUiTzBeAvnEl4SBPLpO238hmZCiqabLgwV";
  var PARSE_KEY = process.env.PARSE_KEY || "QGGox3yxwBWLjzvDeCzYGqyHaxhqKtcEy6uG9oNy";
  var PARSE_MASTER = process.env.PARSE_MASTER || "b0f7TIfgpi7aSxppf0hX1icXxiokDESduuFInsjF";

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
