//购物车控制层
app.controller('cartController', function ($scope, cartService) {
    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.totalValue = cartService.sum($scope.cartList);
            }
        );
    };
    //数量加减
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                if (response.success) {
                    $scope.findCartList();//刷新列表
                } else {
                    alert(response.message)//弹出错误信息
                }
            }
        )
    };
    //获取当前用户的地址列表
    $scope.findAddressList = function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList = response;
                for(var i =0;i<$scope.addressList.length;i++){
                    if ($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
            }
        )
    };

    //选择地址
    $scope.selectAddress =function (address) {
        $scope.address=address;
    };
    //判断某地址是不是当前选中的地址
    $scope.isSelectedAddress = function (address) {
        if ($scope.address == address){
            return true;
        }else{
            return false;
        }
    };
    $scope.order={paymentType:"1"};//订单对象

    //选择支付类型
    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
    };
    //保存订单
    $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address;//当前地址
        $scope.order.receiverMobile = $scope.address.mobile;//手机号
        $scope.order.receiver = $scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(
            function (response) {
                if(response.success){
                    //跳转支付页
                    if ($scope.order.paymentType='1'){//如果是微信支付，跳转到支付页面
                        location.href="pay.html";
                    }else {//如果货到付款，跳转到提示页面
                        location.href="paysuccess.html";
                    }
                }else{
                    alert(response.message)
                }
            }
        )
    };

});
