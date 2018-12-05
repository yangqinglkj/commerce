 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
        //用户注册
        $scope.reg = function () {
            //比较两次输入的密码是否一致
            if($scope.password!=$scope.entity.password){
                alert("两次密码输入不一致，请重新输入");
                $scope.entity.password="";
                return;
            }
            userService.add($scope.entity,$scope.smsCode).success(
                function (response) {
                    alert(response.message);
                }
            )
        };
        //发送验证码
        $scope.sendCode= function () {
            if($scope.entity.phone== null || $scope.entity.phone==""){
                alert("请填写手机号")
            }
            userService.sendCode($scope.entity.phone).success(
                function (response) {
                   alert(response.message)
                }
            )
        }
});	
