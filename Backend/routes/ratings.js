var express = require('express');
var index = require('../index');
var router = express.Router();

// GET all ratings for a specific place
router.get('/ratings/:placeId', function(req, res, next){

    var sql = "SELECT placeId,SUM(rating) FROM UserRating WHERE placeId=?";
    var par = req.params.placeId;

    index.db.query(sql, par, (err, result, fields) => {
        if(err){
            res.send({"success": false, "message": err.message});
            throw err;
        }
        res.send({"success": true, "message": result});
    });
});

// POST rating entered by user for a specific place
router.post('/ratings/set', function(req, res, next){

    var sql = "INSERT INTO UserRating (userId, placeId, rating) VALUES (?, ?, ?)";
    var par = [req.body.userId, req.body.placeId, req.body.rating];

    index.db.query(sql, par, (err, result, fields) => {
        if(err){
            res.send({"success": false, "message": err.message});
            throw err;
        }
        res.send({"success": true, "message": result});
    });
});

module.exports = router;
