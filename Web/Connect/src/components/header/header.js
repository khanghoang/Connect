var React = require('react');
var Navigation = require('../navigation/navigation.js');

if (process.env.BROWSER) {
    require('./header.scss');
}

var Header = React.createClass({

    render: function() {
        return (
            <div className="header-container">
                <div className="logo"></div>
                <Navigation/>
            </div>
        );
    }

});

module.exports = Header;
