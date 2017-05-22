var mongoose = require('mongoose');

var eleSchema   = new mongoose.Schema({
  name: String,
  description : String,
  type : String,
  media : String,  // the path to the media ?
  keywords: [String], // array of tags that pertain to the workout
  //repetitions: Number,
  //sets: Number,
  // if this a cardio lift (ie running, biking, swimming)
  // example for running-> duration: 4 miles, pace 9 min/mile
  //duration: String,
  //pace: String
});

module.exports = mongoose.model('Element', eleSchema);
