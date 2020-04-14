require('dotenv').config();

var express = require('express');
var mysql = require('mysql')
var logger = require('morgan');
var busboy = require('connect-busboy');
var cookieParser = require('cookie-parser');
var compression = require('compression');
var passport = require('passport');
var helmet = require('helmet');
var tunnel = require('tunnel-ssh');

const app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(busboy());
app.use(cookieParser());
app.use(compression());
app.use(helmet());
app.use(passport.initialize());

var mainRouter = require('./routes/main');
var bookmarkRouter = require('./routes/bookmarks');
var ratingRouter = require('./routes/ratings');
var authRouter = require('./routes/auth');

const config = {
  host: 'cs-linux.ncl.ac.uk',
  dstHost: 'db.cs.ncl.ac.uk',
  dstPort: 3306,
  localPort: 33306,
  username: 'username',
  password: 'password',
  keepAlive: true
}

tunnel(config, (error, server) => {
  if (error) {
    console.log("SSH Connection error: " + error);
  }
});

const pool = mysql.createPool({
  connectionLimit: 50,
  host: '127.0.0.1',
  user: 't2022t19',
  password: 'SapsBred.Jab',
  database: 't2022t19',
  port: 33306,
  waitForConnections: true
});

pool.on('error', console.error.bind(console, 'MySQL connection error:'));

app.use('/', mainRouter);
app.use('/', bookmarkRouter);
app.use('/', ratingRouter);
app.use('/auth', authRouter);

app.listen(8080, () => console.log("Server started"))
  .on('error', console.error.bind(console, 'Error:'));

module.exports.pool = pool;