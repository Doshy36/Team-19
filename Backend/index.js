var express = require('express');
var mysql = require('mysql')
var compression = require('compression');
var helmet = require('helmet');
var tunnel = require('tunnel-ssh');

const app = express();
app.use(express.json());
app.use(compression());
app.use(helmet());

var mainRouter = require('./routes/main');
var bookmarkRouter = require('./routes/bookmarks');
var ratingRouter = require('./routes/ratings');
var connection;

const config = {
  host: 'cs-linux.ncl.ac.uk',
  dstHost: 'db.cs.ncl.ac.uk',
  dstPort: 3306,
  localPort: 33306,
  username: 'put-your-uni-username-here',
  password: 'put-your-uni-password-here'
}

tunnel(config, (error, server) => {
  if (error) {
    console.log("SSH Connection error: " + error);
  }

  connection = mysql.createConnection({
    host: '127.0.0.1',
    user: 't2022t19',
    password: 'SapsBred.Jab',
    database: 't2022t19',
    port: 33306
  });
  
  connection.connect();

  module.exports.db = connection;
});


app.use('/', mainRouter);
app.use('/', bookmarkRouter);
app.use('/', ratingRouter);

app.listen(8080, () => console.log("Server started"));