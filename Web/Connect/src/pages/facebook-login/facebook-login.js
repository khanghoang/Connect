var React = require('react');
var Actions = require('../../actions/actions.js');

if (process.env.BROWSER) {
    require('./facebook-login.scss');
}

var FacebookLogin = React.createClass({
    contextTypes: {
        router: React.PropTypes.func.isRequired
    },

    componentDidMount: function() {
        window.fbAsyncInit = function() {
            FB.init({
                appId: '425525177629732',
                xfbml: false,
                version: 'v2.3'
            });

            FB.getLoginStatus(function(response) {
                this.checkLoginState(response);
            }.bind(this));

        }.bind(this);

        // Load the SDK asynchronously
        (function(d, s, id){
            var js, fjs = d.getElementsByTagName(s)[0];
            if (d.getElementById(id)) {return;}

            js = d.createElement(s); js.id = id;
            js.src = '//connect.facebook.net/pt_BR/sdk.js';
            fjs.parentNode.insertBefore(js, fjs);
        }(document, 'script', 'facebook-jssdk'));
    },

    responseApi: function(authResponse) {
        FB.api('/me', function(response) {

            response.status = 'connected';
            response.accessToken = authResponse.accessToken;
            response.expiresIn = authResponse.expiresIn;
            response.signedRequest = authResponse.signedRequest;

            this.loginHandler(response);

        }.bind(this));
    },

    checkLoginState: function(response) {
        if (response.authResponse) {

            this.responseApi(response.authResponse);

        }
    },

    loginHandler: function(response) {
        Actions.login(response);
    },

    handleClick: function() {
        var valueScope = this.props.scope || 'public_profile, email, user_birthday';

        FB.login(this.checkLoginState, { scope: valueScope });
    },

    render: function() {
        return (
            <div className="login-container">
                <div>
                    <button className='facebook-login-button' onClick={ this.handleClick }>
                      Login with Facebook
                    </button>
                    <div id="fb-root"></div>
                </div>
            </div>
        );
    }

});

module.exports = FacebookLogin;
