var React = require('react');
var Router = require('react-router');
var {
  Route,
  DefaultRoute,
  NotFoundRoute,
  RouteHandler,
  Link
} = Router;

var App = require('./components/app/app.js');
var HomePage = require('./pages/home/home.js');
var ChatPage = require('./pages/chat/chat.js');
var LoginPage = require('./pages/facebook-login/facebook-login.js');

module.exports = (
    <Route handler={App} ignoreScrollBehavior>
        <DefaultRoute handler={HomePage} />
        <Route name="index" path="/" handler={LoginPage}>
        </Route>
        <Route name="chat" path="/chat" handler={ChatPage} />
        <Route name="login" path="/login" handler={LoginPage} />
    </Route>
);
