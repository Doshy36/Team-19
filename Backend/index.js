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

const config = {
  host: 'cs-linux.ncl.ac.uk',
  dstHost: 'db.cs.ncl.ac.uk',
  dstPort: 3306,
  localPort: 33306,
  username: 'uni-username-herse',
  password: 'uni-password-here'
}

tunnel(config, (error, server) => {
  if (error) {
    console.log("SSH Connection error: " + error);
  }

  pool = mysql.createPool({
    connectionLimit: 10,
    host: '127.0.0.1',
    user: 't2022t19',
    password: 'SapsBred.Jab',
    database: 't2022t19',
    port: 33306
  });

  module.exports.connection = callback => {
    pool.getConnection((err, connection) => {
      if (err) {
        connection.release();
        throw err;
      }
      callback(connection);
    });
  };

  module.exports.query = (sql, par, callback) => {
    module.exports.connection(connection => {
      connection.query(sql, par, callback);
  
      connection.release();
    });
  };
});


app.use('/', mainRouter);
app.use('/', bookmarkRouter);
app.use('/', ratingRouter);

app.listen(8080, () => console.log("Server started"));