var fithubControllers = angular.module('fithubControllers', []);

fithubControllers.controller('homeControl', ['$scope', '$window', '$location', 'Fit', 'Users', 'Authentication',function($scope,$window,$location, Fit, Users, Authentication) {
	//Setup for navbar
	
	$scope.loggedIn = Authentication.isLoggedIn();
	$scope.userName = Authentication.getUserName();
	$scope.userID = Authentication.getUserID();
	console.log($scope.loggedIn);

	$scope.logout = function(){
		Authentication.userLogout();
		$location.path('/home');
	}

}]);

fithubControllers.controller('signUpControl', ['$scope', '$window', '$location', 'Fit', 'Users', 'Authentication', function($scope,$window,$location, Fit, Users, Authentication) {
	//Setup for navbar
	$scope.loggedIn = Authentication.isLoggedIn();
	$scope.userName = Authentication.getUserName();
	$scope.userID = Authentication.getUserID();

	$scope.logout = function(){
		Authentication.userLogout();
		$location.path('/home');
	}

	$scope.signUpObject = {};

   $scope.signUp = function(){
      Fit.signup($scope.signUpObject).success(function(data){
        console.log(data);
		var param = 'where={"email":"' + $scope.signUpObject.email + '"}';
		Users.customGet(param).success(function(data){
			console.log(data);
			$scope.signUpWorked = true;
			Authentication.userLogin(data.data[0].name, data.data[0]._id);
			$location.path('/explore');
		});
	  }).error(function(data){
	  		console.log("Sign Up failed...");
			$scope.signUpObject = {};
			$scope.signUpWorked = false;
	});
   }
}]);

fithubControllers.controller('createWorkoutControl', ['$scope', '$http', '$window', '$location','Workouts', 'Fit', 'Authentication', 'Users', 'Elements', function($scope, $http, $window, $location,Workouts, Fit, Authentication, Users, Elements) {
	//Setup for navbar
	$scope.loggedIn = Authentication.isLoggedIn();
	$scope.userName = Authentication.getUserName();
	$scope.userID = Authentication.getUserID();

	$scope.logout = function(){
		Authentication.userLogout();
		$location.path('/home');
	}

	Elements.get().success(function(data){
		console.log(data);
		$scope.elements = data;
	})

	$scope.tags = ['lifting', 'cardio', 'sports', 'chest', 'legs', 'back', 'arms', 'endurance', 
	'strength', 'outdoors', 'indoors', 'bodyweight'];

	$scope.workout = {
		name: '',
		description: '',
		num_favorite: 0,
		num_copy: 0,
		original_user: '',
		original_workout_id : '',
		current_user: '',
		current_user_id: '',
		public: false,
		comments: [],
		tags: [],
		days : [
			{
				day: 'Sunday',
				currIndex: 0,
				elements: [
				]
			},
			{
				day: 'Monday',
				currIndex: 0,
				elements: [
				]
			},
			{
				day: 'Tuesday',
				currIndex: 0,
				elements: [
				]
			},
			{
				day: 'Wednesday',
				currIndex: 0,
				elements: [
				]
			},
			{
				day: 'Thursday',
				currIndex: 0,
				elements: [
				]
			},
			{
				day: 'Friday',
				currIndex: 0,
				elements: [

				]
			},
			{
				day: 'Saturday',
				currIndex: 0,
				elements: [
				]
			},
		]
	};

	$scope.workout.original_user = $scope.userName;
	$scope.workout.original_workout_id = '';
	$scope.workout.current_user = $scope.userName;
	$scope.workout.current_user_id = $scope.userID;

	$scope.addToWorkout = function(element, targetDay){
		var elementToAdd = {};
		elementToAdd.name = element.name;
		elementToAdd.elementid = element._id;
		elementToAdd.sets = '';
		elementToAdd.reps = '';
		elementToAdd.time = '';
		elementToAdd.distance = '';
		for(var i = 0; i<$scope.workout.days.length; i++){
			if($scope.workout.days[i].day == targetDay){
				elementToAdd.index = $scope.workout.days[i].currIndex;
				$scope.workout.days[i].elements.push(elementToAdd);
				$scope.workout.days[i].currIndex++;
			}
		}
	};

	$scope.removeFromWorkout = function(elementIndex, targetDay){
		for(var i = 0; i<$scope.workout.days.length; i++){
			if($scope.workout.days[i].day == targetDay){
				for(var j = 0; j < $scope.workout.days[i].elements.length; j++){
					if($scope.workout.days[i].elements[j].index == elementIndex){
						$scope.workout.days[i].elements.splice(j, 1);
					}
				}
			}
		}
	}

	$scope.scrollToTarget = function(target){
		console.log(target);
		$.scrollTo($(target));
	}

	$scope.setActiveElement = function(element){
		$scope.activeElement = element;
		console.log($scope.activeElement);
		Elements.getOne($scope.activeElement.elementid).success(function(data){
			console.log(data);
			$scope.activeSource = data[0].media;
			$scope.activeDescription = data[0].description;
		})
		$('#addedElementModal')
		  .modal('show')
		;
	}

	$scope.setPeekElement = function(element){
		$scope.peekElement = element.name;
		$scope.peekSource = element.media;
		$scope.peekDescription = element.description;
		$('#elementModal')
		  .modal('show')
		;
	}

	$scope.filterQuery = function(element){
		if($scope.query == "" || $scope.query == undefined){
			return true;
		}
		if(element.name.toLowerCase().includes($scope.query.toLowerCase())){
			return true;
		}
		if(element.keywords.indexOf($scope.query.toLowerCase()) != -1){
			return true;
		}
	};

	$scope.toggleTag = function(tag){
		var tagIndex = $scope.workout.tags.indexOf(tag);
		if ( tagIndex == -1){
			$scope.workout.tags.push(tag);
		}else{
			$scope.workout.tags.splice(tagIndex,1);
		}
		console.log($scope.workout.tags);
	}

	$scope.getTagClass = function(tag){
		if ($scope.workout.tags.indexOf(tag) != -1){
			return 'ui button tagButton'
		}else{
			return 'ui button tagButton active'
		}
	}

	$scope.saveModal = function(){
		$('#addedElementModal')
		  .modal('hide')
		;
	}

	$.extend($.scrollTo.defaults, {
	  axis: 'y',
	  duration: 800,
	  offset: -54
	});

	$scope.submit = function(){
		Workouts.add($scope.workout).success(function(data){
			console.log(data);
			Users.customGet('where={"_id":"'+$scope.userID+'"}').success(function(user){
				console.log(user);
				user.data[0].workouts.push(data.data._id);
				Users.put($scope.userID, user.data[0]).success(function(){
					console.log('workout created');
				});
			});
			$location.path('/workout/'+ data.data._id);
		});
	}

}]);


fithubControllers.controller('editWorkoutControl', ['$scope', '$http', '$window', '$location', '$routeParams', 'Workouts', 'Fit', 'Authentication', 'Users', 'Elements', function($scope, $http, $window, $location, $routeParams, Workouts, Fit, Authentication, Users, Elements) {
	$scope.workoutID = $routeParams.id;
	Workouts.getOne($scope.workoutID).success(function(data){
		console.log(data);
		$scope.workout = data.data;
	})

	//Setup for navbar
	$scope.loggedIn = Authentication.isLoggedIn();
	$scope.userName = Authentication.getUserName();
	$scope.userID = Authentication.getUserID();

	$scope.logout = function(){
		Authentication.userLogout();
		$location.path('/home');
	}

	Elements.get().success(function(data){
		console.log(data);
		$scope.elements = data;
	})

	$scope.tags = ['lifting', 'cardio', 'sports', 'chest', 'legs', 'back', 'arms', 'endurance', 
	'strength', 'outdoors', 'indoors', 'bodyweight'];

	$scope.addToWorkout = function(element, targetDay){
		var elementToAdd = {};
		elementToAdd.name = element.name;
		elementToAdd.elementid = element._id;
		elementToAdd.sets = '';
		elementToAdd.reps = '';
		elementToAdd.time = '';
		elementToAdd.distance = '';
		for(var i = 0; i<$scope.workout.days.length; i++){
			if($scope.workout.days[i].day == targetDay){
				elementToAdd.index = $scope.workout.days[i].currIndex;
				$scope.workout.days[i].elements.push(elementToAdd);
				$scope.workout.days[i].currIndex++;
			}
		}
	};

	$scope.removeFromWorkout = function(elementIndex, targetDay){
		for(var i = 0; i<$scope.workout.days.length; i++){
			if($scope.workout.days[i].day == targetDay){
				for(var j = 0; j < $scope.workout.days[i].elements.length; j++){
					if($scope.workout.days[i].elements[j].index == elementIndex){
						$scope.workout.days[i].elements.splice(j, 1);
					}
				}
			}
		}
	}

	$scope.scrollToTarget = function(target){
		console.log(target);
		$.scrollTo($(target));
	}

	$scope.setActiveElement = function(element){
		$scope.activeElement = element;
		console.log($scope.activeElement);
		Elements.getOne($scope.activeElement.elementid).success(function(data){
			console.log(data);
			$scope.activeSource = data[0].media;
			$scope.activeDescription = data[0].description;
			$('#addedElementModal')
			  .modal('show')
			;
		})
	}

	$scope.setPeekElement = function(element){
		$scope.peekElement = element.name;
		$scope.peekSource = element.media;
		$scope.peekDescription = element.description;
		$('#elementModal')
		  .modal('show')
		;
	}

	$scope.filterQuery = function(element){
		if($scope.query == "" || $scope.query == undefined){
			return true;
		}
		if(element.name.toLowerCase().includes($scope.query.toLowerCase())){
			return true;
		}
		if(element.keywords.indexOf($scope.query.toLowerCase()) != -1){
			return true;
		}
	};

	$scope.toggleTag = function(tag){
		var tagIndex = $scope.workout.tags.indexOf(tag);
		if ( tagIndex == -1){
			$scope.workout.tags.push(tag);
		}else{
			$scope.workout.tags.splice(tagIndex,1);
		}
		console.log($scope.workout.tags);
	}

	$scope.saveModal = function(){
		$('#addedElementModal')
		  .modal('hide')
		;
	}

	$scope.getTagClass = function(tag){
		if ($scope.workout.tags.indexOf(tag) != -1){
			return 'ui button tagButton'
		}else{
			return 'ui button tagButton active'
		}
	}

	$.extend($.scrollTo.defaults, {
	  axis: 'y',
	  duration: 800,
	  offset: -54
	});

	$scope.update = function(){
		Workouts.update($scope.workoutID, $scope.workout).success(function(data){
			console.log(data);
			$location.path('/workout/'+ data.data._id);
		});
	}

	$scope.delete = function(){
		console.log('To delete')
		Workouts.delete($scope.workoutID).success(function(data){
			console.log('workout deleted');
			console.log(data);
			$location.path('/profile/'+$scope.userID);
		});
	}


}]);

fithubControllers.controller('workoutControl', ['$scope', '$window', '$location', '$routeParams', "Workouts", 'Fit','Authentication', 'Users', function($scope, $window, $location, $routeParams, Workouts, Fit, Authentication, Users) {
	$scope.workoutid = $routeParams.id;
	$scope.favorited = true;
	//Setup for navbar
	$scope.loggedIn = Authentication.isLoggedIn();
	$scope.userName = Authentication.getUserName();
	$scope.userID = Authentication.getUserID();

	$scope.logout = function(){
		Authentication.userLogout();
		$location.path('/home');
	}

	if($scope.userID != ""){
		Users.customGet('where={"_id":"'+$scope.userID+'"}').success(function(data){
			console.log(data);
			$scope.user = data.data[0];
			if($scope.user.liked_workouts.indexOf($scope.workoutid) == -1){
				$scope.favorited = false;
			}else{
				$scope.favorited = true;
			}
		});
	}
	

	Workouts.getOne($scope.workoutid).success(function(data){
		console.log('get the workout');
		$scope.workout = data.data;
	});

	$scope.tabs = [{
        title: 'Summary',
        url: 'summaryView'
	}, {
        title: 'Detailed',
        url: 'detailedView'
    }];
    $scope.currentTab = 'summaryView';

    $scope.onClickTab = function (tab) {
        $scope.currentTab = tab.url;
    }

    $scope.determineClass = function(tabUrl) {
        if(tabUrl == $scope.currentTab){
        	return 'active item';
        }else{
        	return 'item';
        }
    }

    $scope.copy = function(){
    	$scope.workout.num_copy ++;
		Workouts.update($scope.workoutid, $scope.workout).success(function(data){
			var newWorkout = $scope.workout;
			newWorkout._id = undefined;
			newWorkout.comments = [];
			newWorkout.dateCreated = undefined;
			newWorkout.current_user = $scope.userName;
			newWorkout.current_user_id = $scope.userID;
			Workouts.add(newWorkout).success(function(data){
				console.log(data);
				Users.customGet('where={"_id":"'+$scope.userID+'"}').success(function(user){
					user.data[0].workouts.push(data.data._id);
					Users.put($scope.userID, user.data[0]).success(function (){
						console.log('workoutCopied');
					});
				});
				$location.path('/edit/'+ data.data._id);
			});
		});
	}

	$scope.check_user = function(){
		return ($scope.userID == $scope.workout.current_user_id);
	}

	$scope.favorite = function(){
		$scope.workout.num_favorite ++;
		console.log($scope.workout);
		Workouts.update($scope.workoutid, $scope.workout).success(function(data){
			console.log('update the workout');
			console.log(data);
		});
		Users.customGet('where={"_id":"'+$scope.userID+'"}').success(function(user){
			user.data[0].liked_workouts.push($scope.workout._id);
			Users.put($scope.userID, user.data[0]).success(function (){
				console.log('fav successfully');
				$scope.favorited = true;
			});
		});
	}

	$scope.comment = function(){
		$scope.newCommt = {};
		$scope.newCommt.user = $scope.userName;
		$scope.newCommt.content = $scope.commentBody; //$scope.content binded to front end
		$scope.workout.comments.push($scope.newCommt);
		Workouts.update($scope.workoutid, $scope.workout).success(function(data){
			console.log(data);
			console.log('workout updated with new comment');
		});
	}
}]);

fithubControllers.controller('userProfileControl', ['$scope', '$window', '$location', '$routeParams', '$http', 'Fit', 'Authentication','Workouts','Users', function($scope, $window, $location, $routeParams, $http, Fit, Authentication, Workouts, Users) {
	//Setup for navbar
	$scope.loggedIn = Authentication.isLoggedIn();
	$scope.userName = Authentication.getUserName();
	$scope.userID = Authentication.getUserID();

	$scope.logout = function(){
		Authentication.userLogout();
		$location.path('/home');
	}

	$('.menu .item')
	  .tab()
	;
	/*$scope.check_user = function(){
		return ($scope.userID == $scope.workout.current_user_id);
	}*/
	// get the workouts of the user
	var same_user = ($scope.userID == $routeParams.id);

	Users.customGet('where={"_id":"'+$routeParams.id+'"}').success(function(data){
		console.log(data);
		$scope.user = data.data[0];
	});

	if (same_user){
		Workouts.customGet('where={"current_user_id":"' + $routeParams.id + '"}').success(function(data){
			console.log('get workouts');
			console.log(data);
			$scope.workouts = data;
		});
	}else {
		Workouts.customGet('where={"current_user_id":"' + $routeParams.id + '", "public":true}').success(function(data){
			console.log('get workouts');
			console.log(data);
			$scope.workouts = data;
		});
	}

	$scope.filterByOriginal = function(workout){
		if(workout.current_user == workout.original_user){
			return true;
		}
		return false;
	}

	$scope.filterByCopied = function(workout){
		if(workout.current_user != workout.original_user){
			return true;
		}
		return false;
	}

}]);

fithubControllers.controller('exploreControl', ['$scope','$location','$window','Fit','Authentication','Workouts', function($scope,$location, $window, Fit, Authentication, Workouts) {
	//Setup for navbar
	$scope.loggedIn = Authentication.isLoggedIn();
	$scope.userName = Authentication.getUserName();
	$scope.userID = Authentication.getUserID();

	$scope.logout = function(){
		Authentication.userLogout();
		$location.path('/home');
	}

	$scope.sortParameter = 'num_favorite';
	Workouts.get_public().success(function(data){
		console.log('get workouts');
		console.log(data);
		$scope.workouts = data;
	});

	$scope.filterQuery = function(workout){
		if($scope.query == "" || $scope.query == undefined){
			return true;
		}
		if(workout.name.toLowerCase().includes($scope.query.toLowerCase())){
			return true;
		}
		if(workout.current_user.toLowerCase().includes($scope.query.toLowerCase())){
			return true;
		}
		if(workout.tags.indexOf($scope.query.toLowerCase()) != -1){
			return true;
		}
	};

	$scope.toggleSort = function(sort){
		$scope.sortParameter = sort;
	}



}]);

fithubControllers.controller('loginControl', ['$scope', '$window','$location','Fit', 'Users', 'Authentication', function($scope,$window, $location, Fit, Users, Authentication) {
	//Setup for navbar
	$scope.logout = function(){
		Authentication.userLogout();
		$location.path('/home');
	}
	$scope.loggedIn = Authentication.isLoggedIn();
	$scope.loginObject = {};

	$scope.login = function(){
		Fit.login($scope.loginObject)
			.success(function(data){
				console.log('Login successfully!');
				console.log(data);
				var param = 'where={"email":"' + $scope.loginObject.email + '"}';
				Users.customGet(param).success(function(data){
					console.log(data);
					$scope.LoginWorked = true;
					Authentication.userLogin(data.data[0].name, data.data[0]._id);
					$location.path('/explore');
				});
			}).error(function(data){
				console.log("Unable to log in. Try again");
				$scope.loginObject = {};
				$scope.LoginWorked = false;
			});
	}
}]);
