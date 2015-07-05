var Dispatcher = require('../core/Dispatcher.js');
var Constants = require('../constants/Constants.js');
var ee = require('event-emitter')({});
var CHANGE_EVENT = 'changed';

var _conversations = {};
var _currentConversationID = 1;
var _isContentLoaded = false;
var _currentUser = null;

var Store = {
    setCurrentConversationID: function(conversationID) {
        _currentConversationID = conversationID;
    },

    getMessagesByConversationID: function(conversationID) {
        return _conversations[conversationID];
    },

    getMessages: function() {
        return _conversations[_currentConversationID] || [];
    },

    addConversation: function(conversation) {
        _currentConversationID = conversation._id;
        _conversations[conversation._id] = conversation.messages;
    },

    updateConversation: function(conversation) {
        _isContentLoaded = true;
        _conversations[conversation._id] = conversation.messages;
    },

    addMessage: function(newMessage) {
        _conversations[_currentConversationID].push(newMessage);
    },

    getCurrentUser: function() {
        return _currentUser;
    },

    updateCurrentUser: function(userInfo) {
        _currentUser = userInfo;
    },

    isContentLoaded: function() {
        return _isContentLoaded;
    },

    addListener: function(callback) {
        ee.on(CHANGE_EVENT, callback);
    },

    removeListener: function(callback) {
        ee.off(CHANGE_EVENT, callback);
    }
};

Dispatcher.register(function(action) {
    switch (action.actionType) {
        case Constants.chat.ADD_NEW_MESSAGE:
            Store.addMessage(action.response);
            ee.emit(CHANGE_EVENT);
            break;

        case Constants.chat.ADD_CONVERSATION:
            Store.addConversation(action.response);
            ee.emit(CHANGE_EVENT);
            break;

        case Constants.chat.UPDATE_CHAT_DATA:
            Store.updateConversation(action.response);
            ee.emit(CHANGE_EVENT);
            break;

        case Constants.user.LOGIN:
            Store.updateCurrentUser(action.response);
            ee.emit(CHANGE_EVENT);
            break;
    }
});

module.exports = Store;
