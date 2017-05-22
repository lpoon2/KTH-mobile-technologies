var Ele = require('../models/element.js');

module.exports = function(router) {

router.get('/elements', function(req,res){
Ele.find(function(err, obj){
  if(err){
    res.send(err);
  }
  else{
    res.json(obj);
  }
                    });
});
router.get('/elements/:_id', function(req,res){
Ele.find({_id : req.params._id},function(err, obj){
  if(err){
    res.send(err);
  }
  else{
    res.json(obj);
  }
});
});

router.delete('/elements/:_id', function(req, res){
  Ele.remove({_id : req.params._id}, function(err,obj){
    if(err){
      res.send(err);
    }
    else{
      res.json({message : 'element deleted!'});
    }
  });
});

router.put('/elements/:_id', function(req, res){
  Ele.findByIdAndUpdate(
  req.params._id, req.body,
  function(err,obj){
    if(err){
      res.send(err);
    }
    else{
      res.json(obj);
    }
  });
});
router.post('/elements',function(req,res){
  var ele = new Ele();
  ele.name = req.body.name;
  ele.description = req.body.description;
  ele.type = req.body.type;
  ele.media = req.body.media;
  ele.keywords = req.body.keywords;
  ele.save(function(err, obj){
    if(err){
      res.status(500);
      res.json(err);
    }
    else{
      res.status(201);
      res.json({ message: 'element created!'  });
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
