 //用户表控制层 
app.controller('userController' ,function($scope,userService){
    $scope.entity={}; //需要定义，要不然判断用户为空不能正常执行
//用户注册方法
    $scope.regist=function(){
        //判断用户名是否为空
        if($scope.entity.username==''||$scope.entity.username==null){
            alert("请输入要注册的用户名");
            return;
        }
        //判断用户输入密码和确认密码是否一致
        if($scope.entity.password!=$scope.password){

            alert("对不起两次输入的密码不一致");
            return;
        }
        RemainTime();
        userService.add($scope.entity,$scope.smscode).success(function (response) {
            if(response.success){
                alert("恭喜你注册成功");
            }else {
                alert(response.message);
            }
        })
    }
    //发送验证码
    //发送验证码
    $scope.sendCode=function(){
//判断手机号码是否为空
        if($scope.entity.phone==null||$scope.entity.phone==""){
            alert("请输入手机号码");
            return;
        }
        RemainTime();
        userService.sendCode($scope.entity.phone).success(
            function(response){
                alert(response.message);
            }
        );
    }

});