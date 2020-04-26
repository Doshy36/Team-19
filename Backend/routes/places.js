var express = require('express');
var pool = require('../database');
var router = express.Router();

/* GET a list of all places */
router.get('/places', function(req, res, next) {
    pool.query("SELECT * FROM Place", (err, result, fields) => {
        if (err) {
            res.status(500).send({"success": false, "message": err.message});
            throw err;
        }
        pool.query("SELECT * FROM PlaceCategory", (err2, result2, fields2) => { // Get all categories for each place
            if (err2) {
                res.status(500).send({"success": false, "message": err2.message});
                throw err2;
            }
            result.forEach(row => {
                row.categories = [];
                result2.forEach(category => {
                    if (category.placeId == row.placeId) {
                        row.categories.push(category.categoryId); // Combine categories together
                    }
                });
            });
            res.send({"success": true, "message": result});
        });
    });
});

/* GET a places data */
router.get('/place/:placeId', function(req, res, next) {
    pool.query("SELECT * FROM Place WHERE placeId=?", req.params.placeId, (err, result, fields) => {
        if (err) {
            res.status(500).send({"success": false, "message": err.message});
            throw err;
        }
        if (result.length > 0) {
            pool.query("SELECT categoryId FROM PlaceCategory WHERE placeId=?", req.params.placeId, (err2, result2, fields2) => { // Get all categories of that place
                if (err2) {
                    res.status(500).send({"success": false, "message": err2.message});
                    throw err2;
                }
                result[0].categories = [];
                result2.forEach(row => {
                    result[0].categories.push(row.categoryId); // Combine categories together
                })
                res.send({"success": true, "message": result[0]});
            });
        } else {
            res.status(400).send({"success": false, "message": "No place exists with that ID"});
        }
    });
});

module.exports = router;
