var fitServices=angular.module("fitServices",[]);
// TODO Change this
var baseURL = "http://localhost:3000/api";

fitServices.factory("Fit",function($http,$window,$location){
  return{
    signup : function(userObject){
      return $http.post(baseURL + '/signup', userObject);
    },
    login : function(userObject){
      return $http.post(baseURL + '/login', userObject);
    },
    logout: function(){
    	$window.sessionStorage.isLogedin = "false";
    	$window.sessionStorage.user_id = "";
    	$window.sessionStorage.user_name = "";
    	$location.path('/home');
	},
  }
});


// Users
fitServices.factory("Users", function($http){
	return{
		get: function() {
			//var sessionID = $window.sessionStorage
			return $http.get(baseURL + "/users");
		},
		getOne: function(userID) {
			//var sessionID = $window.sessionStorage
			console.log(baseURL + "/users/" + userID);
			return $http.get(baseURL + "/users/" + userID);
		},
		add: function(data){
			return $http.post(baseURl+'/users', data);
		},
		delete: function(userID) {
			//var sessionID = $window.sessionStorage.id
			return $http.delete(baseURL + "/users/" + userID);
		},
		customGet: function(parameters){
			return $http.get(baseURL + "/users?" + parameters);
		},
		put: function(userID, data){
			return $http.put(baseURL + "/users/" + userID, data);
		}

	}
});


// Elements
fitServices.factory("Elements", function($http){
	return{
		get: function() {
			//var sessionID = $window.sessionStorage
			return $http.get(baseURL + "/elements");
		},
		getOne: function(elementID) {
			//var sessionID = $window.sessionStorage
			return $http.get(baseURL + "/elements/" + elementID);
		},
		add: function(elementName, elementDesc, elementType, elementMedia, elementKeywords){
			data = {
				name: elementName,
				description: elementDesc,
				type: elementType,
				media: elementMedia,
				keywords: elementKeywords
			}
			return $http.post(baseURl+'/elements', data);
		},
		delete: function(elementID) {
			//var sessionID = $window.sessionStorage
			return $http.delete(baseURL + "/elements/" + elementID);
		},
		customGet: function(parameters){
			return $http.get(baseURL + "/elements?" + parameters);
		}
	}
});


// GET all workouts
fitServices.factory("Workouts", function($http){
	return{
		get: function() {
			//var sessionID = $window.sessionStorage
			return $http.get(baseURL + "/workout");
		},
		getOne: function(workoutID) {
			//var sessionID = $window.sessionStorage
			return $http.get(baseURL + "/workout/" + workoutID);
		},
		add: function(data){
			return $http.post(baseURL+'/workout', data);
		},
		// TODO Update FIX THIS
		update: function(workoutid, data){
			return $http.put(baseURL+'/workout/' + workoutid, data);
		},
		delete: function(workoutID) {
			//var sessionID = $window.sessionStorage
			return $http.delete(baseURL + "/workout/" + workoutID);
		},
		customGet: function(parameters){
			console.log(baseURL + "/workout?" + parameters);
			return $http.get(baseURL + "/workout?" + parameters);
		},
		get_public : function(){
	      return $http.get(baseURL + "/workout/public");
	    }
	}
});

fitServices.factory("Authentication", function($http){
	var loggedIn = false;
	var loggedUserName = "";
	var loggedUserID = "";
	return{
		isLoggedIn: function(){
			return loggedIn;
		},
		userLogin: function(name, id){
			loggedIn = true;
			loggedUserName = name;
			loggedUserID = id;
		},
		userLogout: function(){
			loggedIn = false;
			loggedUserName = "";
			loggedUserID = "";
		},
		getUserName: function(){
			return loggedUserName;
		},
		getUserID: function(){
			return loggedUserID;
		},
	}
});
