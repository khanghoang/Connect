var path = require('path');
var fs = require('fs');
var express = require('express');
var _ = require('lodash');
var React = require('react');
var Router = require('react-router');
var server = express();

server.set('port', (process.env.PORT || 3001));
server.use(express.static(path.join(__dirname, 'public')));

//
// Register server-side rendering middleware
// -----------------------------------------------------------------------------

// The top-level React component + HTML template for it
var templateFile = path.join(__dirname, 'templates/index.html');
var template = _.template(fs.readFileSync(templateFile, 'utf8'));

server.get('*', function(req, res, next) {

    var data = {
        title: '',
        body: ''
    };

    var html = template(data);
    res.send(html);
});

server.listen(server.get('port'), function() {
    if (process.send) {
        process.send('online');
    } else {
        console.log('The server is running at http://localhost:' + server.get('port'));
    }
});
