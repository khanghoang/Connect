var React = require('react');

if (process.env.BROWSER) {
    require('./loading.scss');
}

var Loading = React.createClass({

    render: function() {
        return (
            <div className="spinner">
                <div className="spinner-container container1">
                    <div className="circle1"></div>
                    <div className="circle2"></div>
                    <div className="circle3"></div>
                    <div className="circle4"></div>
                </div>
                <div className="spinner-container container2">
                    <div className="circle1"></div>
                    <div className="circle2"></div>
                    <div className="circle3"></div>
                    <div className="circle4"></div>
                </div>
                <div className="spinner-container container3">
                    <div className="circle1"></div>
                    <div className="circle2"></div>
                    <div className="circle3"></div>
                    <div className="circle4"></div>
                </div>
            </div>
        );
    }

});

module.exports = Loading;
