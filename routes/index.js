module.exports = function (app, router) {

  app.use('/api', require('./api_element.js')(router));
  app.use('/api', require('./api_workout.js')(router));
  app.use('/api', require('./login.js')(router));
  app.use('/api', require('./api_workoutid')(router));
  // user routes
  app.use('/api', require('./users.js')(router));
  // user id routes
  app.use('/api', require('./users_id.js')(router));

};
