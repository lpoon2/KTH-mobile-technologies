var work = require('../models/workout.js');

module.exports = function(router) {

  function ErrMsg(error){
    var msg = "Error: ";
    if (!error.errors){
      msg += error.message;
    }else{
      //var space = true;
      for (var key in error.errors){
        msg += error.errors[key].message + ' ';
      }
    }
    return msg;
  }

router.get('/workout/public', function(req, res){
work.find({public : true }, function(err, obj){
if(err){
    res.send(err);
}
  else{
    res.json(obj);
  }
});

});  

router.get('/workout', function(req,res){
  var selectObject = {};
  var queryObject = {};
  if (req.query.where){
    queryObject = JSON.parse(req.query.where);
  }
  if (req.query.select){
    selectObject = JSON.parse(req.query.select);
  }
  work.find(queryObject, selectObject, function(err, obj){
  if(err){
    res.send(err);
  }
  else{
    res.json(obj);
  }
                    });
});

/*router.get('/workout/:current_user', isLoggedIn, function(req,res){
work.find({current_user: req.user.name},function(err, obj){
  if(err){
    res.send(err);
  }
  else{
    res.json(obj);
  }
                    });
});*/


router.post('/workout',function(req,res){
  var returnObject = {};
  var ele = new work();
  console.log(req.body);
  //Entire new workout object
  ele.name = req.body.name;
  ele.description = req.body.description;
  ele.original_user = req.body.original_user;//!!!!!!!!!!!!!!!!!!!!!*original user need to be changed to null if created or previous user
  //ele.original_user_id = req.body.original_user_id;
  ele.current_user = req.body.current_user;//current user need to be changed in front end
  ele.current_user_id = req.body.current_user_id;
  ele.original_workout_id = req.body.original_workout_id;//!!!!!!!!!!!!!!!!!!!!!*taken care in front end
  ele.public = req.body.public;
  ele.num_favorite = 0;
  ele.num_copy = 0;
  ele.tags = req.body.tags;
  ele.comments = req.body.comments;
  ele.days = req.body.days;//is this enough to make a deep copy ?
  
  
  ele.save(function(err){
    if(err){
      console.log(err.message);
      returnObject.message = err.message;
      returnObject.data = {};
      res.status(500);
    }
    else{
      returnObject.message = "Workout successfully created";
      returnObject.data = ele;
      res.status(201);
    }
    res.send(returnObject);
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
