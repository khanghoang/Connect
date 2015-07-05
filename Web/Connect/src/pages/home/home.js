var React = require('react');

if (process.env.BROWSER) {
    require('./home.scss');
}

var Carousel = require('../../components/carousel/carousel.js');

var Home = React.createClass({

    render: function() {
        return (
            <div>
            aaaa
                <Carousel />
            </div>
        );
    }

});

module.exports = Home;
