var Dispatcher = require('../core/Dispatcher.js');
var Constants = require('../constants/Constants.js');
var API = require('../core/API.js');

var initializeSocket = function(roomID, token) {
    if (typeof io !== 'undefined') {
        window.socket = io.connect(window.baseUrl, {
            query: 'token=' + token
        });

        socket.on('updateChat', function(msg) {
            Dispatcher.dispatch({
                actionType: Constants.chat.ADD_NEW_MESSAGE,
                response: msg
            });
        });

        socket.on('log', function(type, message) {
            console.log(type, message);
        });

        socket.on('joinRoomSuccessfully', function(data) {

            Dispatcher.dispatch({
                actionType: Constants.chat.UPDATE_CHAT_DATA,
                response: data
            });
        });

        socket.emit('joinRoom', roomID);
    }
}

var startConversation = function(currentUserInfo) {
    var targetUserId = window.shopID;
    API.createConversation(currentUserInfo.token, targetUserId).then(function(conversation) {
        Dispatcher.dispatch({
            actionType: Constants.chat.ADD_CONVERSATION,
            response: conversation
        });

        initializeSocket(conversation._id, currentUserInfo.token);
    });
}

var actions = {
    addNewMessage: function(newMessage) {
        socket.emit('sendChat', newMessage);
    },

    login: function(credential) {
        API.fbLogin(credential.accessToken).then(function(userInfo) {
            Dispatcher.dispatch({
                actionType: Constants.user.LOGIN,
                response: userInfo
            });

            startConversation(userInfo);
        });
    },

    createConversation: function(bearerToken, targetUserId) {
        API.createConversation(bearerToken, targetUserId)
    },

    follow: function(token) {
        API.follow(token, window.shopID).then(function(userInfo) {
            Dispatcher.dispatch({
                actionType: Constants.user.FOLLOW
            });
        });
    }
};

module.exports = actions;
