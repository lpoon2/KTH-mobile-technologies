var express = require('express');
var app = express();
var router = express.Router();
var bodyParser = require('body-parser');
var mongoose = require('mongoose');

var passport = require('passport');
require('./passport')(passport);

var flash    = require('connect-flash');
var session      = require('express-session');

app.use(express.static(__dirname + '/public'));

var allowCrossDomain = function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "X-Requested-With, X-HTTP-Method-Override, Content-Type, Accept");
  res.header("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
  next();
};
app.use(allowCrossDomain);
// required for passport
app.use(session({
  secret: 'DM2518finalProject',
  resave: false,
  saveUninitialized: false
 }));

 app.use(passport.initialize());
 app.use(passport.session()); // persistent login sessions
 app.use(flash());
 app.use(bodyParser.urlencoded({
  extended: true
}));
app.use(bodyParser.json());

var port = process.env.PORT || 3000;

mongoose.connect("mongodb://fithub:dm2518@ds161487.mlab.com:61487/fithub");

require('./routes')(app, router);

console.log("Express server running on " + port);
app.listen(port);
