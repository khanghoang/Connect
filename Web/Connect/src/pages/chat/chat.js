var React = require('react');
var ChatComponent = require('../../components/chat/chat.js');
var Store = require('../../stores/Store.js');
var Actions = require('../../actions/actions.js');

var ChatPage = React.createClass({
    getInitialState: function() {
        return {
            messages: Store.getMessages()
        };
    },

    componentDidMount: function() {
        Store.addListener(this._handleChanged);
    },

    componentWillUnmount: function() {
        Store.removeListener(this._handleChanged);
    },

    _handleChanged: function() {
        this.setState({
            messages: Store.getMessages()
        });
    },

    _handleUserSubmit: function(newMessage) {
        Actions.addNewMessage(newMessage);
    },

    _handleUserClickFollow: function(e) {
        Actions.follow(Store.getCurrentUser().token);
    },

    render: function() {
        return (
            <ChatComponent messages={this.state.messages}
                onUserClickFollow={this._handleUserClickFollow}
                onUserSubmit={this._handleUserSubmit} />
        );
    }

});

module.exports = ChatPage;
