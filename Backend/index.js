var express = require('express');

const app = express();

var testRouter = require('./routes/test');

app.use('/', testRouter);

app.listen(8080, () => console.log("Server started"));