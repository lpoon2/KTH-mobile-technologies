var mongoose = require('mongoose');

var elemDetail = new mongoose.Schema({
  name: String,
  index: Number,
  elementid: String,
  reps: String,
  sets: String,
  time: String,
  distance: String,
});
var workoutSchema   = new mongoose.Schema({
  name: String,
  description : String,
  num_favorite : Number,
  num_copy : Number,
  original_user : String,
  original_workout_id : String,
  current_user : String,
  current_user_id :String,
  rating : Number,
  public: Boolean,
  dateCreated: {
    type: Date,
    default: Date.now
  },
  comments : [
    {
      user: String,
      content: String,
    }
  ],
  tags: [String], // array of tags that pertain to the workout
  //elements : [String], //array of element_ids and fields(ie weight, reps, sets, etc
  days : [
    {
      day:String,
      currIndex: Number,
      elements: [elemDetail],
    }
  ]

});

module.exports = mongoose.model('Workout', workoutSchema);
