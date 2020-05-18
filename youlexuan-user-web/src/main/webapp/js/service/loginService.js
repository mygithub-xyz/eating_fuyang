//登录Service
app.service('loginService',function($http){
	//显示登录人名字
	this.showName=function(){
		return  $http.get('../login/name.do');
	}

});