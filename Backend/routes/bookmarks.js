var express = require('express');
var index = require('../index');
var router = express.Router();

// GET all bookmarks for a particular user
router.get('/', function(req, res, next){

    var sql = "SELECT * FROM UserBookmark WHERE userId=?";
    var par = req.user;

    index.pool.query(sql, par, (err, result, fields) => {
        if(err){
            res.json({"success": false, "message": err.message});
            throw err;
        }
        res.json({"success": true, "message": result});
    });
});

// POST bookmark specified by the user
router.post('/add', function(req, res, next){

    var sql = "INSERT INTO UserBookmark (userId, placeId) VALUES (?, ?)";
    var par = [req.body.userId, req.body.placeId];

    index.pool.query(sql, par, (err, result, fields) => {
        if(err){
            res.json({"success": false, "message": err.message});
            throw err;
        }
        res.json({"success": true, "message": result});
    });
});

// DELETE user bookmark
router.delete('/delete/:placeId', function(req, res, next) {

    var sql = "DELETE FROM t2022t19.UserBookmark WHERE userId=? AND placeId=?";
    var par = [req.user, req.params.placeId];

    index.pool.query(sql, par, (err, result, fields) => {
        if(err) {
            res.json({"success": false, "message": err.message});
            throw err;
        }
        res.status(204).json({"success": true});
    });
});

module.exports = router;