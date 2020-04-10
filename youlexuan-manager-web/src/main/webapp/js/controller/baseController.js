app.controller('baseController', function ($scope) {//依赖注入服务


    $scope.reloadList=function(){
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }


    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载.调用的起始处
        }
    };


    //初始化复选框值的array
    $scope.selectIds=[];
    /**
     * 选中取消,selectIds增加删除
     * 如何判断是选中还是取消:angularJs提供了内置的对象$event中可以去除是勾选还是取消
     */
    $scope.updateSelection=function ($event, id) {
        if($event.target.checked){//勾选
            $scope.selectIds.push(id);
        }else{//取消
            //根据id找到对应的index
            var index = $scope.selectIds.indexOf(id);
            //根据index从数组中删除
            $scope.selectIds.splice(index, 1);
        }
    }

    //扩展
    $scope.selectAll = function () {

        if ($scope.select_all) { //判断是全选
            $scope.selectIds = [];//先清空，防止在操作了一个轮回之后，重复添加了...
            angular.forEach($scope.list, function (i) {  //list.devices这是循环从后台获取的数组，并添加到刚刚定义的数组里
                i.checked = true; //全选即将所有的复选框变为选中
                $scope.selectIds.push(i.id);
            })
        }else{
            angular.forEach($scope.list, function (i) {
                i.checked = false; //所有复选框为不选中
                $scope.selectIds = [];    })
        }
    }
});