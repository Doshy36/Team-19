var index = require('../index');
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
		.isLength({min: 8}).withMessage("Password too short"),
], async function(req, res, next) {
	passport.authenticate('register', {session: false}, (err, user, info) => {
		if (err) {
			res.status(401).json({"success": false, "message": err.message});
		} else if (user) {
			const accessToken = jwt.sign({ id: user }, process.env.ACCESS_SECRET, { expiresIn: '15m'});
			const refreshToken = jwt.sign({ id: user }, process.env.REFRESH_SECRET, { expiresIn: '30d' });
			res.cookie('rTkn', refreshToken, {
				path: '/auth',
				httpOnly: true,
				secure: process.env.NODE_ENV === 'production',
				maxAge: 30 * 24 * 60 * 60,
				sameSite: true
			});
			res.status(201).json({"success": true, "message": "Successfully registered user", "accessToken": accessToken});
		} else {
			res.status(401).json({"success": false, "message": info.message});
		}
	})(req, res, next)
});

router.post('/login', [
	check('email', 'Invalid email address').exists().withMessage("No email provided")
		.isEmail().withMessage("Using invalid email"),
	check('password', 'Password is invalid').exists().withMessage("No password provided")
], async function(req, res, next) {
	passport.authenticate('login', {session: false}, (err, user, info) => {
		if (err) {
			res.status(401).json({"success": false, "message": err.message});
		} else if (user) {
			const accessToken = jwt.sign({ id: user }, process.env.ACCESS_SECRET, { expiresIn: '15m'});
			const refreshToken = jwt.sign({ id: user }, process.env.REFRESH_SECRET, { expiresIn: '30d' });
			res.cookie('rTkn', refreshToken, {
				path: '/auth',
				httpOnly: true,
				secure: process.env.NODE_ENV === 'production',
				maxAge: 30 * 24 * 60 * 60,
				sameSite: true
			});
			res.status(200).json({"success": true, "message": "Successfully logged in", "accessToken": accessToken});
		} else {
			res.status(401).json({"success": false, "message": info.message});
		}
	})(req, res, next)
});

router.post('/token', async function(req, res, next) {
	const rTkn = req.cookies.rTkn;
	if (rTkn == null) {
		return res.status(401).json({"success": false, "message": "Unauthorized"});
	}
	jwt.verify(rTkn, process.env.REFRESH_SECRET, (err) => {
		if (err) {
			return res.status(403).json({"success": false, "message": "Forbidden"});
		}
	});
	const userId = jwt.decode(rTkn).id;
	index.pool.query('SELECT 1 FROM `UserToken` WHERE token=?', rTkn, (err, results) => {
		if (err) {
			return res.status(401).json({"success": false, "message": err});
		} else if (results.length > 0) {
			return res.status(403).json({"success": false, "message": "Forbidden"});
		} else {
			const accessToken = jwt.sign({ id: userId }, process.env.ACCESS_SECRET, { expiresIn: '15m'});
			res.status(200).json({"success": true, "accessToken": accessToken});
		}
	})
});

router.get('/logout', passport.authenticate('jwt', {session: false}), async function(req, res, next) {
	const rTkn = req.cookies.rTkn;

	if (rTkn == null) {
		return res.status(401).json({"success": false, "message": "Unauthorized"});
	}
	jwt.verify(rTkn, process.env.REFRESH_SECRET, (err) => {
		if (err) {
			return res.status(403).json({"success": false, "message": "Forbidden"});
		}
	});

	index.pool.query('INSERT INTO `UserToken` (userId,token) VALUES (?,?)', [req.user,rTkn], (err, results) => {
		if (err) {
			res.clearCookie('rTkn').status(200).json({"success": true, "message": err});
		} else {
			req.logout();
			res.clearCookie('rTkn').status(200).json({"success": true, "message": "Logged out"});
		}
	});
});

module.exports = router;
