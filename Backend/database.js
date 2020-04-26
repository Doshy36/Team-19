var mysql = require('mysql');
var tunnel = require('tunnel-ssh');

var active = false;

const config = {
    host: 'cs-linux.ncl.ac.uk',
    dstHost: 'db.cs.ncl.ac.uk',
    dstPort: 3306,
    localPort: process.env.DB_PORT,
    username: process.env.SSH_USER,
    password: process.env.SSH_PASS,
    keepAlive: true
}
  
if (!active) {
    tunnel(config, (error) => {
        if (error) {
            console.log("SSH Connection error: " + error);
        }
        active = true;
    }).on('error', console.error.bind(console, 'SSH connection error:'));
}

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

module.exports = pool;