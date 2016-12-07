/** @module users/router */
'use strict';

var express = require('express');
var router = express.Router();
var middleware =  require('../middleware');
var mysql      = require('mysql');


//supported methods
router.all('/', middleware.supportedMethods('GET, POST'));

router.get('/', function(req, res, next) {
  res.render('main');
});


router.post('/', function(req,res,next){

    var query = req.body.query;

    console.log("query: "+query);

    if(query.length > 0) {
        var exec = require('child_process').exec;
        // var runSearch = 'java -jar TwitterSE.jar '+query;
        var runSearch = 'java -jar TwitterSE.jar /"' + query + '"/';
        console.log(runSearch);


        exec(runSearch, {maxBuffer: 1024 * 8000}, function (error, stdout, stderr) {
            if (stdout) {

                var out = JSON.parse(stdout);

                var response = {
                    data: out
                };
                console.log(out.length);
                res.json(response);
            }
        });
    };
});
module.exports = router;