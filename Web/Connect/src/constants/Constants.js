var keyMirror = require('react/lib/keyMirror');

module.exports = {
    chat: keyMirror({
        ADD_NEW_MESSAGE: null,
        ADD_CONVERSATION: null,
        UPDATE_CHAT_DATA: null
    }),

    user: keyMirror({
        LOGIN: null,
        FOLLOW: null
    })
};
