var expect = require('chai').expect;

var Image = require('../../src/components/image/image');
var createComponent = require('../utils/create-component');

describe('Image component', function() {
    var image;

    beforeEach(function() {
        image = createComponent(Image, { src: 'https://www.google.com/images/srpr/logo11w.png'});
    });

    it('should render a image with src', function() {
        const imageSrc = image.props.children[0];

        expect(imageSrc.props.children).to.equal('https://www.google.com/images/srpr/logo11w.png');
    });
});
