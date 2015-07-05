var React = require('react');
var Store = require('../../stores/Store.js');
var Actions = require('../../actions/actions.js');

if (process.env.BROWSER) {
    require('./app.scss');
}

var ChatPage = require('../../pages/chat/chat.js');
var LoginPage = require('../../pages/facebook-login/facebook-login.js');

var App = React.createClass({

    getInitialState: function() {
        return {
            user: Store.getCurrentUser()
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
            user: Store.getCurrentUser()
        });
    },

    render: function() {
        var content = <LoginPage />;

        if (this.state.user) {
            content = <ChatPage />;
        }

        return (
            <div className="wrapper">
                {content}
            </div>
        );
    }

});

module.exports = App;
