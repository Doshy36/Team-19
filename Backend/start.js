const app = require('./index');

app.listen(8080, () => console.log("Server started")) // Start the server
  .on('error', console.error.bind(console, 'Error:'));