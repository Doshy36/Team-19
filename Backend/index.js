require('dotenv').config();

var express = require('express');
var logger = require('morgan');
var busboy = require('connect-busboy');
var cookieParser = require('cookie-parser');
var compression = require('compression');
var passport = require('passport');
var helmet = require('helmet');

const app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(busboy());
app.use(cookieParser());
app.use(compression());
app.use(helmet());
app.use(passport.initialize());


var placeRouter = require('./routes/places');
var bookmarkRouter = require('./routes/bookmarks');
var ratingRouter = require('./routes/ratings');
var authRouter = require('./routes/auth');

app.use('/', placeRouter);
app.use('/bookmarks', passport.authenticate('jwt', {session: false}), bookmarkRouter);
app.use('/', ratingRouter);
app.use('/auth', authRouter);

module.exports = app;