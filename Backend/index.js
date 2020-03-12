var express = require('express');
var mysql = require('mysql')

const app = express();
app.use(express.json());

var testRouter = require('./routes/ratings');

var connection = mysql.createConnection({
  host: '127.0.0.1',
  user: 't2022t19',
  password: 'SapsBred.Jab',
  database: 't2022t19',
  port: 33306
});

connection.connect();

app.use('/', testRouter);

app.listen(8080, () => console.log("Server started"));

module.exports.db = connection;