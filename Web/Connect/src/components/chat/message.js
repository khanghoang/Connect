var React = require('react');
var Avatar = require('../image/image.js');

if (process.env.BROWSER) {
    require('./message.scss');
}

var Message = React.createClass({

    render: function() {
        var userAvatar;
        var classes = 'isOwner';

        if (this.props.data.user && this.props.data.user.avatar) {
            classes = '';
            userAvatar = (
                <div className="avatar-container">
                    <Avatar classes="user-avatar" src={this.props.data.user.avatar} />
                </div>
            );
        }

        return (
            <div className={'message-container ' + classes}>
                {userAvatar}
                <div className="message">{this.props.data.content}</div>
            </div>
        );
    }

});

module.exports = Message;
