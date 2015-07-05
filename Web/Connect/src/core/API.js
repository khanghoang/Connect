var request = require('superagent');

window.baseUrl = 'https://connect.ngrok.com';

// window.baseUrl = 'https://konnect-staging.herokuapp.com';
var BASE_URL = window.baseUrl;

var FB_LOGIN_URL = BASE_URL + '/login/facebookLogin';
var GET_PROFILE_URL = BASE_URL + '/api/user/current';
var CREATE_CONVERSATION_URL = BASE_URL + '/api/conversation/create';
var FOLLOW_URL = BASE_URL + '/api/user/follow';
var UNFOLLOW_URL = BASE_URL + '/api/user/unfollow';

var makeRequest = function(method, url, accessToken, params, body) {
    var requestObject = request(method, url)
    .type('form')
    .query(params)
    .send(body);

    if(accessToken) {
        requestObject = requestObject.set('Authorization', 'Bearer ' + accessToken);
    }

    return requestObject;
};

module.exports = {
    getProfile: function(bearerToken) {
        return new Promise(function(resolve, reject) {
            makeRequest('GET', GET_PROFILE_URL, bearerToken).end(function(err, res) {
                if (err) {
                    reject(err);
                }

                var profile = JSON.parse(res.text).data;
                profile.token = bearerToken;
                resolve(profile);
            })
        })
    },

    fbLogin: function(fbAccessToken) {
        return new Promise(function(resolve, reject) {
            makeRequest('POST', FB_LOGIN_URL, false, {}, { access_token: fbAccessToken }).end(function(err, res) {
                if (err) {
                    reject(err);
                }

                var data = JSON.parse(res.text);

                resolve(data);
            });
        });
    },

    createConversation: function(bearerToken, targetUserId) {
        return new Promise(function(resolve, reject) {
            makeRequest('POST', CREATE_CONVERSATION_URL, bearerToken, {}, {target_user_id: targetUserId}).end(function(err, res) {
                if (err) {
                    reject(err);
                }

                var conversation = JSON.parse(res.text).data;
                resolve(conversation);
            })
        })
    },

    follow: function(bearerToken, targetUserId) {
        console.log(bearerToken);
        return new Promise(function(resolve, reject) {
            makeRequest('POST', FOLLOW_URL, bearerToken, {}, {user_id: targetUserId}).end(function(err, res) {
                if (err) {
                    reject(err);
                }

                var result = JSON.parse(res.text).data;
                resolve(result);
            })
        })
    }
}
