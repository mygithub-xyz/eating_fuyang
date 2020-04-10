app.controller('brandController', function ($scope,$controller, brandService) {//依赖注入服务


    //继承baseController,本质上继承的是$scope
    $controller('baseController',{$scope:$scope});

    $scope.findAll=function () {
        brandService.findAll().success(//把$http.get()业务代码分离到了到了brandService中,在此处用brandService调用
            function (response) {
                $scope.list = response;
            }
        );
    }

    $scope.findPage=function (page, rows) {
        brandService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;

                //把后台的总条数给$scope.paginationConf
                $scope.paginationConf.totalItems = response.total;
            }
        );
    }

    /**
     * 新增,及修改
     * 品牌从$scope双向绑定域获取
     */
    $scope.save=function () {

        //要区分是新增还是修改,brand.id
        if($scope.brand.id  == null){
            if ($scope.brand.name!=null){
                brandService.add($scope.brand).success(//ng-model brand.name,brand.firstChar
                    function (response) {
                        if(response.success){
                            //新增成功,查询列表
                            alert(response.message);
                            $scope.reloadList();
                        }else{
                            alert(response.message);
                        }
                    }
                );
            }else {
                alert("请输入添加的品牌吧！！！")
            }

        }else{
            brandService.update($scope.brand).success(//ng-model brand.name,brand.firstChar
                function (response) {
                    if(response.success){
                        //新增成功,查询列表
                        alert(response.message);
                        $scope.reloadList();
                    }else{
                        alert(response.message);
                    }
                }
            );
        }


    }

    /**
     * 根据id查询品牌
     */
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.brand = response;
            }
        );
    }

    /**
     * 删除
     */
    $scope.dele=function () {
        if ($scope.selectIds.length>0){
            brandService.dele($scope.selectIds).success(
                function (response) {
                    if(response.success){
                        //新增成功,查询列表
                        alert(response.message);
                        $scope.selectIds=[];
                        $scope.reloadList();
                    }else{
                        alert(response.message);
                    }
                }
            )
        }else {
            alert("请先选择，再删除吧！！");
        }

    }

    $scope.searchEntity = {};
    /**
     * 条件查询
     */
    $scope.search=function (page, rows) {
        brandService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;

                //把后台的总条数给$scope.paginationConf
                $scope.paginationConf.totalItems = response.total;
            }
        );
    }
});
