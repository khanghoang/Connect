var React = require('react');
var ChatList = require('./chat-list.js');
var ChatHeader = require('./chat-header.js');
var Input = require('./input.js');

if (process.env.BROWSER) {
    require('./chat.scss');
}

var Chat = React.createClass({
    propTypes: {
        messages: React.PropTypes.array
    },

    getInitialState: function() {
        return {
            isCollapsed: false
        };
    },

    getDefaultProps: function() {
        return {
            messages: []
        };
    },

    _handleUserClickMinWindow: function() {
        this.setState({
            isCollapsed: !this.state.isCollapsed
        });
    },

    render: function() {
        var classes = '';
        var shopName = window.shopName;
        if (this.state.isCollapsed) {
            classes += 'collapsed';
        }

        return (
            <div className={'chat-container ' + classes}>
                <ChatHeader shopName={shopName}
                    onUserClickHeader={this._handleUserClickMinWindow}
                    onUserClickFollow={this.props.onUserClickFollow}
                    onUserClickMinWindow={this._handleUserClickMinWindow} />

                <ChatList messages={this.props.messages} />
                <Input onUserSubmit={this.props.onUserSubmit}/>
            </div>
        );
    }

});

module.exports = Chat;
