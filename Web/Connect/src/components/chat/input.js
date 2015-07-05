var React = require('react');

var Input = React.createClass({

    getInitialState: function() {
        return {
            value: ''
        };
    },

    _handleKeyUp: function(e) {
        var inputElement = React.findDOMNode(this.refs.input);
        var inputValue = inputElement.value;

        // user press enter
        if (e.keyCode === 13) {
            inputElement.value = '';
            inputValue && this.props.onUserSubmit && this.props.onUserSubmit(inputValue);
        }
    },

    render: function() {
        return (
            <div className="chat-input-container">
                <input type="text" ref="input" onKeyUp={this._handleKeyUp}/>
            </div>
        );
    }

});

module.exports = Input;
