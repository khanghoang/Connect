var React = require('react');
var assign = require('lodash.assign');
var Loading = require('../loading/loading.js');

if (process.env.BROWSER) {
    require('./image.scss');
}

var Image = React.createClass({
    propTypes: {
        src: React.PropTypes.string.isRequired
    },

    getInitialState: function() {
        return {
            loading: true
        };
    },

    componentDidMount: function() {
        this.checkValidImage();
    },

    checkValidImage: function() {
        var image = document.createElement('img');
        image.src = this.props.src;
        image.onerror = this._handleError;
        image.onload = this._handleLoad;
    },

    _handleLoad: function() {
        this.setState({
            loading: false
        });
    },

    _handleError: function() {
        this.setState({
            loading: true
        });
    },

    render: function() {
        if (this.state.loading) {
            return <Loading />;
        }

        var styles = {
            backgroundImage: 'url("' + this.props.src + '")',
            backgroundSize: 'cover'
        };

        styles = assign(styles, this.props.inlineStyles);

        var classes = 'image ' + (this.props.classes || '');

        return <div className={classes} style={styles}></div>;
    }
});

module.exports = Image;
