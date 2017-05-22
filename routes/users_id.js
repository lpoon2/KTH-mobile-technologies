var User = require('../models/user.js');
var mongoose = require('mongoose');
mongoose.Promise = global.Promise;

module.exports = function(router) {
	var userIDsRoute = router.route('/users/:id');
	// get request
	userIDsRoute.get(function(req,res){
		User.findById(req.params.id,function(err,response){
			if (err){
				res.status(500);
				res.send({
					message: "Error in getting user",
					data: []
				});
			}
			else{
				if (response == null || response == undefined){
					res.status(404);
					res.send({
						message: "Unable to get user",
						data: []
					});
				}
				else{
					res.status(200);
					res.json({
						message: "Got the user",
						data: response
					});
				}
			}
		});
	});
	// put call
	userIDsRoute.put(function(req, res) {
	console.log("The req body for put request is: ");
	console.log(req.body);
	User.findByIdAndUpdate(req.params.id, {$set: req.body}, {new: true}, function(err, response) {
		if (err) {
			res.status(500);
			res.send({
				message: "Unable to update user",
				data: []
			});
		}
		else {
			if (response == null || response == undefined) {
				console.log("got bad response");
				res.status(404);
				res.send({
					message: "User not found",
					data: []
				});
			}
			else {
				console.log("all clear");
				res.status(200);
				res.json({
					message: "User has been updated",
					data: response
				});
			}
		}
	});
});

	// delete call Careful
	userIDsRoute.delete(function(req, res) {
		User.findByIdAndRemove(req.params.id, function(err, response) {
			if (err) {
				res.status(500);
				res.send({
					message: "Unable to remove the user",
					data: []
				});
			}
			else {
				if (response == null || response == undefined) {
					res.status(404);
					res.send({
						message: "Unable to find the user to delete",
						data: []
					});
				}
				else {
					res.status(200);
					res.json({
						message: "User has been deleted",
						data: response
					});
				}
			}
		});
	});
	return router;
}
// route middleware to make sure a user is logged in
// function isLoggedIn(req, res, next) {

// // if user is authenticated in the session, carry on
// if (req.isAuthenticated()){

//     return next();
// }
// // if they aren't redirect them to the home page
// res.redirect('/');
// }
