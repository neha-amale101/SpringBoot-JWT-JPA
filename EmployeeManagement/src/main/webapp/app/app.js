;
		(function() {
			function authInterceptor(API, auth) {				
				return {
					// automatically attach Authorization header
					request : function(config) {
						// skip register and login web calls
						if (config.url.indexOf(API + "/register") === 0) {
							// Skip this call
						} else if (config.url.indexOf(API + "/authenticate") === 0) {
							// Skip this call
						} else // Attach token to all other http request as
								// auth header
						{
							var token = auth.getToken();
							if (config.url.indexOf(API) === 0 && token) {
								config.headers.Authorization = 'Bearer '
										+ token;
							}
						}
						return config;
					},

					response : function(res) {
						if (res.config.url.indexOf(API) === 0 && res.data.token) {
							auth.saveToken(res.data.token);
						}

						return res;
					},
				}
			}

			function authService($window) {
				var srvc = this;

				srvc.parseJwt = function(token) {
					var base64Url = token.split('.')[1];
					var base64 = base64Url.replace('-', '+').replace('_', '/');
					return JSON.parse($window.atob(base64));
				};

				srvc.saveToken = function(token) {
					$window.localStorage['jwtToken'] = token
				};

				srvc.logout = function(token) {
					$window.localStorage.removeItem('jwtToken');
				};

				srvc.getToken = function() {
					return $window.localStorage['jwtToken'];
				};

				srvc.isAuthed = function() {
					var token = srvc.getToken();
					if (token) {
						var params = srvc.parseJwt(token);
						return Math.round(new Date().getTime() / 1000) <= params.exp;
					} else {
						return false;
					}
				}

			}

			function userService($http, API, auth) {
				var srvc = this;
				
				srvc.getAllEmployee = function() {				
					return $http.get(API + '/getAllEmployee')
				}
				
				srvc.addEmployee = function(employee) {
					if(employee.id)
				    {
						
						return $http.put(API + '/updateEmployee', employee)
				    }
					else
					{	
						return $http.post(API + '/addEmployee', employee)
					}
				}
				
				srvc.editEmployee = function(employee) {
										
					return $http.put(API + '/updateEmployee', employee)
				}
				
				srvc.deleteEmployee = function(employee) {
					return $http.delete(API + '/deleteEmployee/'+ employee.id)
				}
				
				srvc.register = function(email, password) {
				
					return $http.post(API + '/register', {
						email : email,
						password : password,
					});
				};

				srvc.login = function(email, password) {
					return $http.post(API + '/authenticate', {
						email : email,
						password : password
					});
				};

			}

			
			// We won't touch anything in here
			function MainCtrl(user, auth) {
				var self = this;
				
				var isAuthed = auth.isAuthed(); 
				
				if(isAuthed)
				{
				 	user.getAllEmployee().then(handleAllEmployee,handleAllEmployee);
				}
				
				function handleRequest(res) {
				
					var token = res.data ? res.data.token : null;
					if (token) {
						console.log('JWT:', token);
					}					
					self.message = res.data.message;
				}

				function handleAllEmployee(res) {
					var employees = res.data.data ? res.data.data : null;
					self.employees = employees;
					self.message = res.data.message;
				}
				
				function handleAddEmployee(res) {
				
					user.getAllEmployee().then(handleAllEmployee,handleAllEmployee);
					self.employee ={};
				}
		
				function handleDeleteEmployee(res) {
				
					user.getAllEmployee().then(handleAllEmployee,handleAllEmployee);
					self.employee ={};
				}
				self.login = function() {
					user.login(self.email, self.password).then(handleRequest,
							handleRequest)							
				}
				
				self.register = function() {
					user.register(self.email, self.password).then(
							handleRequest, handleRequest)
				}
				
				self.getAllEmployee = function() {
					user.getAllEmployee().then(handleAllEmployee,handleAllEmployee)
				}
				
				self.addEmployee = function() {
					user.addEmployee(self.employee).then(handleAddEmployee,handleAddEmployee);
				}						
				
				self.initAddEmployee = function() {					
					self.employees = self.getAllEmployee();
					self.employee = {};					
				}
				
				self.initUpdateEmployee = function(employee) {				
					self.employee = employee;
				}
				
				self.editEmployee = function() {
					user.editEmployee(self.employee).then(handleRequest,
							handleRequest)
				}
				
				self.deleteEmployee = function(employee) {					
					self.employee = employee;
					user.deleteEmployee(self.employee).then(handleDeleteEmployee,handleDeleteEmployee)
				}
				
				self.logout = function() {
					auth.logout && auth.logout()
				}
				self.isAuthed = function() {
					return auth.isAuthed ? auth.isAuthed() : false
				}
			}
				
			
			angular.module('app', [ 'ngResource' ])
			.factory('authInterceptor',authInterceptor)
			.service('user', userService)
			.service('auth', authService)			
			.constant('API','http://localhost:8080')
			.config(function($httpProvider) {
				$httpProvider.interceptors.push('authInterceptor');	})
			.controller('Main', MainCtrl)			
		})();
