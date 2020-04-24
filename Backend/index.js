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
  localPort: process.env.DB_PORT,
  username: process.env.SSH_USER,
  password: process.env.SSH_PASS,
  keepAlive: true
}

tunnel(config, (error, server) => {
  if (error) {
    console.log("SSH Connection error: " + error);
  }
}).on('error', console.error.bind(console, 'SSH connection error:'));

const pool = mysql.createPool({
  connectionLimit: 50,
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_DB,
  port: process.env.DB_PORT,
  waitForConnections: true
});

pool.on('error', console.error.bind(console, 'MySQL connection error:'));

app.use('/', mainRouter);
app.use('/bookmarks', passport.authenticate('jwt', {session: false}), bookmarkRouter);
app.use('/', ratingRouter);
app.use('/auth', authRouter);

app.listen(8080, () => console.log("Server started"))
  .on('error', console.error.bind(console, 'Error:'));

module.exports.pool = pool;