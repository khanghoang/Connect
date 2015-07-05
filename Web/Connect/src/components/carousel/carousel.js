var React = require('react');
var Carousel = require('nuka-carousel');
var ItemTemplate = require('./templates/champions-league-2015.js');

var MCarousel = React.createClass({
    mixins: [Carousel.ControllerMixin],
    getDefaultProps: function() {
        return {
            items: []
        };
    },

    renderItems: function() {
        return this.props.items.map(function(item) {
            return <ItemTemplate item={item} />;
        });
    },

    render: function() {
        return (
            <div>
                <Carousel ref="carousel" data={this.setCarouselData.bind(this, 'carousel')}>
                    {this.renderItems()}
                </Carousel>
            </div>
        );
    }

});

module.exports = MCarousel;
