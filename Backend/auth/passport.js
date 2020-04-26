var pool = require('../database');

var passport = require('passport');
var bcrypt = require('bcrypt');
var uuid = require('uuid');

var JwtStrategy = require('passport-jwt').Strategy,
    LocalStrategy = require('passport-local').Strategy,
    ExtractJwt = require('passport-jwt').ExtractJwt;

// Middleware for authentication and authorization

passport.use('register', new LocalStrategy(
    {
        usernameField: 'email', 
        session: false 
    },
    async function(email, password, done) {
        try {
            pool.query('SELECT 1 FROM `User` WHERE email=?', email, async (err, results) => { // Check if a user exists already
                if (err) {
                    done(null, false, {message: err});
                } else {
                    if (results.length > 0) {
                        done(null, false, {message: "A user already exists with that email"});
                    } else {
                        const userId = uuid.v4();
                        const passwordSalt = await bcrypt.genSalt(10);
                        const passwordHash = await bcrypt.hash(password, passwordSalt); // hash and salt password
                        pool.query("INSERT INTO `User` (userId,email,passwordHash,passwordSalt) VALUES (?,?,?,?)", [userId, email, passwordHash, passwordSalt], (err) => {
                            if (err) {
                                done(err, false, {message: err})
                            } else {
                                done(null, userId); // pass userId to the request object
                            }
                        });
                    }
                }
            });
        } catch (err) {
            done(err);
        }
}));

passport.use('login', new LocalStrategy(
    {
        usernameField: 'email', 
        session: false 
    },
    async function(email, password, done) {
        try {
            pool.query('SELECT userId,passwordHash,passwordSalt FROM `User` WHERE email=?', email, async (err, results) => { // Find a user attached to the email
                if (err) {
                    done(null, false, {message: err}); // Stop if error
                } else {
                    if (results.length > 0) { // A user is found
                        var passCorrect = await bcrypt.compare(password, results[0].passwordHash); // Check hash matches
                        if (passCorrect) {
                            done(null, results[0].userId); // Continue and pass user to the request
                        } else {
                            done(null, false, {message: "Incorrect email or password"});
                        }
                    } else {
                        done(null, false, {message: "Incorrect email or password"});
                    }
                }
            });
        } catch (err) {
            done(err);
        }
}));

passport.use('jwt', new JwtStrategy(
    {
        jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(), // Authorization header that is sent on requests
        secretOrKey: process.env.ACCESS_SECRET, // The secret that is used to encrypt/decrypt jwt tokens
    }, 
    async function(token, done) {
        try {
            pool.query('SELECT 1 FROM `User` WHERE userId=?', token.id, async (err, results) => { // Check if the user exists
                if (err) {
                    done(null, false, {message: err});
                } else {
                    if (results.length > 0) {
                        done(null, token.id); // Set the user id in the request
                    } else {
                        done(null, false);
                    }
                }
            });
        } catch (err) {
            done(err);
        }
}));