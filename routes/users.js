var User = require('../models/user.js');
var mongoose = require('mongoose');
mongoose.Promise = global.Promise;

module.exports = function(router) {
	var usersRoute = router.route('/users');


	usersRoute.options(function(req, res){
		res.writeHead(200);
		res.end();
	});

	
// get request

	usersRoute.get(function(req,res){
		console.log("In get request");
		// I just added the same JSON encoded query string as MP4
		// I forgot how Arjun said to do it but this is how I did my MP4 and makes sense to me so
		// Ill keep it for now
		var countQuery = req.query.count;

		var whereQuery = req.query.where;
		if (whereQuery != undefined)
			whereQuery = eval("("+whereQuery+")");

		var sortQuery = req.query.sort;
		if (sortQuery != undefined)
			sortQuery = eval("("+sortQuery+")");

		var selectQuery = req.query.select;
		if (selectQuery != undefined)
			selectQuery = eval("("+selectQuery+")");

		var limitQuery = req.query.limit;
		if (limitQuery != undefined)
			limitQuery = eval("("+limitQuery+")");

		var skipQuery = req.query.skip;
		if(skipQuery != undefined)
			skipQuery = eval("("+skipQuery+")");

		var query = User.find(whereQuery).limit(limitQuery).skip(skipQuery).sort(sortQuery).select(selectQuery).
		exec(function(err,result){
			if(err){
				console.log("in error of get");
				res.status(500);
				res.send({
					message: "Error unable to get users",
					data: []
				});
			}
			else{
				console.log("in else so we have no errors");
				// check if the result actually contain something
				if( result.length == 0){
					res.status(200);
					res.send({
						message: "The data base has no users",
						data: []
					});
				}
				else if (countQuery != undefined && (String(countQuery).valueOf() == String(true).valueOf() ||  eval("("+countQuery+")") == "true") ){
					res.status(200);
					res.json({
						message:"Got the users",
						data: result.length
					});
				}
				else{
					console.log("send back the result ");
					res.json({
						message: "Got the users",
						data: result
					});
				}
			}
		});
	});
	return router;
}
// route middleware to make sure a user is logged in
function isLoggedIn(req, res, next) {

// if user is authenticated in the session, carry on
if (req.isAuthenticated()){

    return next();
}
// if they aren't redirect them to the home page
res.redirect('/');
}
