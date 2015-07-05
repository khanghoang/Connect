var React = require('react');
var Message = require('./message.js');
var Loading = require('../loading/loading.js');
var Store = require('../../stores/Store.js');
var Actions = require('../../actions/actions.js');

var ChatList = React.createClass({
    getInitialState: function() {
        return {
            isLoading: !Store.isContentLoaded()
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
            isLoading: !Store.isContentLoaded()
        });
    },

    componentDidUpdate: function() {
        var node = this.getDOMNode();
        node.scrollTop = node.scrollHeight;
    },

    renderMessages: function() {
        if (this.state.isLoading) {
            return <Loading/>;
        }

        return this.props.messages.map(function(message) {
            return <Message key={message._id} data={message} />;
        });
    },

    render: function() {
        return (
            <div className="chat-list-container">
                {this.renderMessages()}
            </div>
        );
    }

});

module.exports = ChatList;
