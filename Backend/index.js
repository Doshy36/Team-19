var express = require('express');
var mysql = require('mysql')

const app = express();

var testRouter = require('./routes/main');

var connection = mysql.createConnection({
  host: '127.0.0.1',
  user: 't2022t19',
  password: 'SapsBred.Jab',
  database: 't2022t19'
});

connection.connect();

app.use('/', testRouter);

app.listen(8080, () => console.log("Server started"));

module.exports.db = connection;