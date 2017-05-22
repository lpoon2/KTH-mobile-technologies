var mongoose = require('mongoose');
var bcrypt   = require('bcrypt-nodejs');

var userSchema   = mongoose.Schema({
  name: String,
  email: {type : String, unique : true},
  workouts : [String],
  Hash_password : String,
  liked_workouts :[String],
});

// methods ======================
// generating a hash
userSchema.methods.generateHash = function(password) {
    return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
};

// checking if password is valid
userSchema.methods.validPassword = function(password) {
    return bcrypt.compareSync(password, this.Hash_password);
};

module.exports = mongoose.model('User', userSchema);
