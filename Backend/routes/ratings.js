var express = require('express');
var pool = require('../database');
var router = express.Router();
var passport = require('passport');

// GET average rating for a specific place
router.get('/ratings/:placeId', function(req, res, next){
    pool.query("SELECT placeId,AVG(rating) FROM UserRating WHERE placeId=?", req.params.placeId, (err, result, fields) => { // Get the average rating
        if(err){
            res.send({"success": false, "message": err.message});
            throw err;
        }
        res.send({"success": true, "message": result});
    });
});

// POST rating entered by user for a specific place
router.post('/ratings/set', passport.authenticate('jwt', {session: false}), function(req, res, next){
    var sql = "INSERT INTO UserRating (userId, placeId, rating) VALUES (?, ?, ?)";
    var par = [req.user, req.body.placeId, req.body.rating];

    pool.query("SELECT 1 FROM Place WHERE placeId=?", req.body.placeId, (err, result, fields) => {
        if (err) {
            res.status(500).json({"success": false, "message": err.message});
            throw err;
        }
        if (result.length > 0) { // If place exists
            pool.query(sql, par, (err, result, fields) => {
                if(err){
                    res.send({"success": false, "message": err.message});
                    throw err;
                }
                res.send({"success": true, "message": result});
            });
        } else {
            res.status(400).json({"success": false, "message": "No place exists with that ID"});
        }
    })
});

module.exports = router;
