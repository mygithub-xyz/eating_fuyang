
app.controller('cartController', function ($scope, cartService) {
    $scope.showName=function(){
        cartService.showName().success(
            function (response) {
                $scope.loginName=response.loginName;
            }
        )}
//查询购物车列表
    $scope.findCartList=function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                //求合计
                $scope.totalValue = cartService.sum($scope.cartList);
            }
        );
    }
   //添加购物车
    $scope.addGoodsToCartList=function (itemId , num) {
        cartService.addGoodsToCartList(itemId , num).success(

            function (response) {
                if(response.success){
                    //查询购物车列表
                    $scope.findCartList();
                }else{
                    alert(response.message);
                }
            }
        );
    }
//获取地址列表
    $scope.findAddressList=function(){
        cartService.findAddressList().success(
            function(response){
                $scope.addressList=response;
                //设置默认地址
                for(var i=0;i< $scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
            });
    }
//选择地址
    $scope.selectAddress=function(address){
        $scope.address=address;
    }

    //判断是否是当前选中的地址
    $scope.isSelectedAddress=function(address){
        if(address==$scope.address){
            return true;
        }else{
            return false;
        }
    }

    $scope.order={paymentType:'1'};
//选择支付方式
    $scope.selectPayType=function(type){
        $scope.order.paymentType= type;
    }

   //保存订单
   $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//电话
        $scope.order.receiver=$scope.address.contact;//联系人
       cartService.submitOrder($scope.order).success(
           function (response) {
            if (response.success){
             //页面跳转
             if ($scope.order.paymentType=='1'){
            //扫码支付跳转到支付页面
                 location.href='pay.html';
             }else {
             //货到付款,跳转到提示页面
                 location.href='paysuccess.html';
             }
            }else {
                alert(response.message);
            }
           }
       );
   }

});