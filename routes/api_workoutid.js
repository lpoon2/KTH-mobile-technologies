/**
 * Created by ChangXu on 11/30/16.
 */
var workout = require('../models/workout.js');

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

    router.get('/workout/:id', function(req,res){
        workout.findById(req.params.id, function(error, result){
            if (error){
                res.status(500);
                res.json({message: ErrMsg(error)});
            }else if (!result){
                res.status(404);
                res.json({message: 'workout Not Found'});
            }else {
                res.status(200);
                res.json({message: 'OK', data: result});
            }
        });
    });

    router.put('/workout/:id',function(req,res){
        var returnObject = {};
        workout.findById(req.params.id, function(error, ele){
            if (error){
                returnObject.message = error.message;
                returnObject.data = {};
                res.status(500);
            }
            if (!ele){
                res.status(404);
                res.json({message: 'workout Not Found'});
                return;
            }

              ele.name = req.body.name;
              ele.description = req.body.description;
              ele.original_user = req.body.original_user;//!!!!!!!!!!!!!!!!!!!!!*original user need to be changed to null if created or previous user
              ele.original_user_id = req.body.original_user_id;
              ele.current_user = req.body.current_user;//current user need to be changed in front end
              ele.current_user_id = req.body.current_user_id;
              ele.original_workout_id = req.body.original_workout_id;//!!!!!!!!!!!!!!!!!!!!!*taken care in front end
              ele.public = req.body.public;
              ele.num_favorite = req.body.num_favorite;
              ele.num_copy = req.body.num_copy;
              ele.tags = req.body.tags;
              ele.comments = req.body.comments;
              ele.days = req.body.days;//is this enough to make a deep copy ?

            //elements : [String], //array of element_ids and fields(ie weight, reps, sets, etc

            ele.save(function(err){
                if(err){
                    returnObject.message = err.message;
                    returnObject.data = {};
                }
                else{
                    returnObject.message = "OK";
                    returnObject.data = ele;
                    res.status(200);
                }
                res.send(returnObject);
            });
        });
    });

    router.delete('/workout/:id', function(req,res){
        workout.findByIdAndRemove(req.params.id, function(error, result){
            if (error){
                res.status(500);
                res.json({message: ErrMsg(error), data: []});
            }else if (!result){
                res.status(404);
                res.json({message: 'workout Not Found', data:[]});
            }else {
                res.status(200);
                res.json({message: 'workout deleted', data: []});
            }
        });
    });
    return router;
}
