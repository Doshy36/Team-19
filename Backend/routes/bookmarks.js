var express = require('express');
var pool = require('../database');
var router = express.Router();

// GET all bookmarks for a particular user
router.get('/', function(req, res, next){

    var sql = "SELECT * FROM UserBookmark WHERE userId=?";
    var par = req.user;

    pool.query(sql, par, (err, result, fields) => {
        if(err){
            res.status(500).json({"success": false, "message": err.message});
            return;
        }
        res.json({"success": true, "message": result});
    });
});

// POST bookmark specified by the user
router.post('/add', function(req, res, next){

    var sql = "INSERT INTO UserBookmark (userId, placeId) VALUES (?, ?)";
    var par = [req.user, req.body.placeId];

    pool.query("SELECT 1 FROM Place WHERE placeId=?", req.body.placeId, (err, result, fields) => {
        if (err) {
            res.status(500).json({"success": false, "message": err.message});
            return;
        }
        if (result.length > 0) { // Place exists
            pool.query(sql, par, (err, result, fields) => {
                if(err){
                    res.status(400).json({"success": false, "message": err.message});
                    return;
                }
                res.json({"success": true, "message": result});
            });
        } else {
            res.status(400).json({"success": false, "message": "No place exists with that ID"});
        }
    })
});

// DELETE user bookmark
router.delete('/delete/:placeId', function(req, res, next) {

    var sql = "DELETE FROM t2022t19.UserBookmark WHERE userId=? AND placeId=?";
    var par = [req.user, req.params.placeId];

    pool.query("SELECT 1 FROM Place WHERE placeId=?", req.params.placeId, (err, result, fields) => {
        if (err) {
            res.status(500).json({"success": false, "message": err.message});
            return;
        }
        if (result.length > 0) { // Place exists
            pool.query(sql, par, (err, result, fields) => {
                if(err) {
                    res.status(500).json({"success": false, "message": err.message});
                    return;
                }
                res.status(200).json({"success": true, "message": "Removed bookmark from user"});
            });
        } else {
            res.status(400).json({"success": false, "message": "No place exists with that ID"});
        }
    })
});

module.exports = router;