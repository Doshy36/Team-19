var express = require('express');
var index = require('../index');
var router = express.Router();

// GET all bookmarks for a particular user
router.get('/bookmarks/:userId', function(req, res, next){

    var sql = "SELECT * FROM UserBookmark WHERE userId=?";
    var par = req.params.userId;

    index.db.query(sql, par, (err, result, fields) => {
        if(err){
            res.send({"success": false, "message": err.message});
            throw err;
        }
        res.send({"success": true, "message": result});
    });
});

// POST bookmark specified by the user
router.post('/bookmarks', function(req, res, next){

    var sql = "INSERT INTO UserBookmark (userId, placeId) VALUES (?, ?)";
    var par = [req.body.userId, req.body.placeId];

    index.db.query(sql, par, (err, result, fields) => {
        if(err){
            res.send({"success": false, "message": err.message});
            throw err;
        }
        res.send({"success": true, "message": result});
    });
});

// DELETE user bookmark
router.post('/bookmarks', function(req, res, next){

    var sql = "DELETE FROM t2022t19.UserBookmark WHERE userId=? AND placeId=?";
    var par = [req.body.userId, req.body.placeId];

    index.db.query(sql, par, (err, result, fields) => {
        if(err){
            res.send({"success": false, "message": err.message});
            throw err;
        }
        res.send({"success": true, "message": result});
    });
});

module.exports = router;