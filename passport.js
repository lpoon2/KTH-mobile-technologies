var User       = require('./models/user');
var LocalStrategy   = require('passport-local').Strategy;
var auth = require('passport-local-authenticate');

module.exports = function(passport) {
/**   serializer  & deserializer
                start
**/
  // used to serialize the user for the session
  passport.serializeUser(function(user, done) {

      done(null, user.id);
  });

  // used to deserialize the user
  passport.deserializeUser(function(id, done) {

      User.findById(id, function(err, user) {

          done(err, user);
      });
  });
  /**   serializer & deserializer
                  End
  **/
  passport.use('local-signup', new LocalStrategy({
         // by default, local strategy uses username and password, we will override with email
         usernameField : 'email',
         passwordField : 'Hash_password',
         passReqToCallback : true // allows us to pass back the entire request to the callback
     },
     function(req, email, password, done) {

         // asynchronous
         // User.findOne wont fire unless data is sent back
         process.nextTick(function() {

         // find a user whose email is the same as the forms email
         // we are checking to see if the user trying to login already exists
         User.findOne({ 'Hash_password' :  email }, function(err, user) {
             // if there are any errors, return the error
             if (err)
                 return done(err);

             // check to see if theres already a user with that email
             if (user) {
                 return done(null, false);
             } else {

               var newUser = new User();
               newUser.name = req.body.name;
               newUser.email   = email;
               newUser.Hash_password = newUser.generateHash(password);
               //newUser.Hash_password  = auth.hash(password);
               newUser.save(function(err) {
                       if (err)
                           throw err;
                       return done(null, newUser);
                   });
             }
         });
       });
     }));

//passport.use('local-login', new LocalStrategy(

passport.use('local', new LocalStrategy(
  {
         usernameField : 'email',
         passwordField : 'Hash_password',
         passReqToCallback : true // allows us to pass back the entire request to the callback
  },
 function(req,email, password, done) {
   User.findOne({ 'email': email }, function (err, user) {
  if (err) { return done(err); }
  if (!user) {
      return done(null, false);
  }

  if( !user.validPassword(password)) { return done(null, false); }
  //if (!user.verifyPassword(password)) { return done(null, false); }
  return done(null, user);
});
}

));

};
