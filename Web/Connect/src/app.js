var React = require('react');
var Router = require('react-router');
var routes = require('./routes.js');

var rootDocument = document.getElementById('app-connect');

// local
window.shopID = '559897facc52c7421e28dde1';
window.shopName = 'Orange Fashion';

// staging
// window.yourID = '5597c63988c7c70501c49c43';
// window.shopName = 'Orange Fashion';

Router.run(routes, Router.HistoryLocation, function(Handler) {
    React.render(<Handler/>, rootDocument);
});
