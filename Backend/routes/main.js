var express = require('express');
var index = require('../index');
var router = express.Router();

/* GET a list of all places */
router.get('/places', function(req, res, next) {
    index.db.query("SELECT * FROM Place", (err, result, fields) => {
        if (err) {
            res.send({"success": false, "message": err.message});
            throw err;
        }
        index.db.query("SELECT * FROM PlaceCategory", (err2, result2, fields2) => {
            if (err2) {
                res.send({"success": false, "message": err2.message});
                throw err2;
            }
            result.forEach(row => {
                row.categories = [];
                result2.forEach(category => {
                    if (category.placeId == row.placeId) {
                        row.categories.push(category.categoryId);
                    }
                });
            });
            res.send({"success": true, "message": result});
        });
    });
});

/* GET a places data */
router.get('/place/:placeId', function(req, res, next) {
    console.log(req.params.placeId);
    index.db.query("SELECT * FROM Place WHERE placeId=?", req.params.placeId, (err, result, fields) => {
        if (err) {
            res.send({"success": false, "message": err.message});
            throw err;
        }
        index.db.query("SELECT categoryId FROM PlaceCategory WHERE placeId=?", req.params.placeId, (err2, result2, fields2) => {
            if (err) {
                res.send({"success": false, "message": err.message});
                throw err;
            }
            result[0].categories = [];
            result2.forEach(row => {
                result[0].categories.push(row.categoryId);
            })
            res.send({"success": true, "message": result[0]});
        });
    });
});

module.exports = router;
