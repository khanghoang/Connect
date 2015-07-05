var React = require('react');

var ChatHeader = React.createClass({

    render: function() {
        return (
            <div className="chat-header" onClick={this.props.onUserClickHeader}>
                <div className="shop-name">{this.props.shopName}</div>

                <div className="window-buttons-container">
                    <div className="follow-button" onClick={this.props.onUserClickFollow}></div>
                    <div className="minimize-button" onClick={this.props.onUserClickMinWindow}></div>
                </div>
            </div>
        );
    }

});

module.exports = ChatHeader;
