var pool = require('../database');

var passport = require('passport');
var bcrypt = require('bcrypt');
var uuid = require('uuid');

var JwtStrategy = require('passport-jwt').Strategy,
    LocalStrategy = require('passport-local').Strategy,
    ExtractJwt = require('passport-jwt').ExtractJwt;

passport.use('register', new LocalStrategy(
    {
        usernameField: 'email', 
        session: false 
    },
    async function(email, password, done) {
        try {
            pool.query('SELECT 1 FROM `User` WHERE email=?', email, async (err, results) => {
                if (err) {
                    done(null, false, {message: err});
                } else {
                    if (results.length > 0) {
                        done(null, false, {message: "A user already exists with that email"});
                    } else {
                        const userId = uuid.v4();
                        const passwordSalt = await bcrypt.genSalt(10);
                        const passwordHash = await bcrypt.hash(password, passwordSalt);
                        pool.query("INSERT INTO `User` (userId,email,passwordHash,passwordSalt) VALUES (?,?,?,?)", [userId, email, passwordHash, passwordSalt], (err) => {
                            if (err) {
                                done(err, false, {message: err})
                            } else {
                                done(null, userId);
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
            pool.query('SELECT userId,passwordHash,passwordSalt FROM `User` WHERE email=?', email, async (err, results) => {
                if (err) {
                    done(null, false, {message: err});
                } else {
                    if (results.length > 0) {
                        var passCorrect = await bcrypt.compare(password, results[0].passwordHash);
                        if (passCorrect) {
                            done(null, results[0].userId);
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
        jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
        secretOrKey: process.env.ACCESS_SECRET,
    }, 
    async function(token, done) {
        try {
            pool.query('SELECT 1 FROM `User` WHERE userId=?', token.id, async (err, results) => {
                if (err) {
                    done(null, false, {message: err});
                } else {
                    if (results.length > 0) {
                        done(null, token.id);
                    } else {
                        done(null, false);
                    }
                }
            });
        } catch (err) {
            done(err);
        }
}));