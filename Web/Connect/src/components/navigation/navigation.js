var React = require('react');

var Router = require('react-router');
var { Link } = Router;

if (process.env.BROWSER) {
    require('./navigation.scss');
}

var Navigation = React.createClass({

    render: function() {
        return (
            <div className="navigation-container">
                <ul>
                    <li>
                        <Link to="videos">Videos</Link>
                    </li>
                    <li>
                        <Link to="schedule">Schedule</Link>
                    </li>
                </ul>
            </div>
        );
    }

});

module.exports = Navigation;
