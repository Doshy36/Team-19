const { check } = require('express-validator'); 

var passport = require('passport');
var jwt = require('jsonwebtoken');

require('../auth/passport');

var express = require('express');
var router = express.Router();

router.post('/register', [
	check('email', 'Invalid email address').exists().withMessage("No email provided")
		.isEmail().withMessage("Using invalid email"),
	check('password', 'Password is invalid').exists().withMessage("No password provided")
		.isLength({min: 8}).withMessage("Password too short"), // Requirement checks for user and pass
], async function(req, res, next) {
	passport.authenticate('register', {session: false}, (err, user, info) => { // Use middleware to authenticate
		if (err) {
			res.status(401).json({"success": false, "message": err.message});
		} else if (user) {
			const accessToken = jwt.sign({ id: user }, process.env.ACCESS_SECRET, { expiresIn: '30d'}); // Build access token that's useable for 30 days
			res.status(201).json({"success": true, "message": "Successfully registered user", "accessToken": accessToken, "userId": user});
		} else {
			res.status(401).json({"success": false, "message": info.message});
		}
	})(req, res, next)
});

router.post('/login', [
	check('email', 'Invalid email address').exists().withMessage("No email provided")
		.isEmail().withMessage("Using invalid email"),
	check('password', 'Password is invalid').exists().withMessage("No password provided") // Requirement checks for user and pass
], async function(req, res, next) {
	passport.authenticate('login', {session: false}, (err, user, info) => { // Use middleware to authenticate
		if (err) {
			res.status(401).json({"success": false, "message": err.message});
		} else if (user) {
			const accessToken = jwt.sign({ id: user }, process.env.ACCESS_SECRET, { expiresIn: '30d'}); // Build access token that's useable for 30 days
			res.status(200).json({"success": true, "message": "Successfully logged in", "accessToken": accessToken, "userId": user});
		} else {
			res.status(401).json({"success": false, "message": info.message});
		}
	})(req, res, next)
});

module.exports = router;
