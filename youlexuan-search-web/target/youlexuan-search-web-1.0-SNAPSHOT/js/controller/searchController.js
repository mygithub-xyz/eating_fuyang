app.controller('searchController',function($scope,$location,searchService) {
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':20,'sortField':'','sort':'' };//搜索对象
    $scope.resultMap={};
    //加载查询字符串
    $scope.loadkeywords=function(){
        $scope.searchMap.keywords= $location.search()['keywords'];
        if ($scope.searchMap.keywords!=null){
            $scope.search();
        }
    }
    //搜索
    $scope.search = function () {
        $scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;//搜索返回的结果
                buildPageLabel();//调用
            }
        );
    }
    //添加搜索项
    $scope.addSearchItem=function(key,value){
        if(key=='category' || key=='brand'||key=='price'){//如果点击的是分类或者是品牌
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    };
    //移除搜索项
    $scope.removeSearchItem=function(key){
        if(key=='category' || key=='brand'||key=='price'){//如果点击的是分类或者是品牌
            $scope.searchMap[key]='';
        }else{
            delete $scope.searchMap.spec[key];//仅删除spec对象key对应的属性
        }
        $scope.search();
    };
    //移除搜索项
    $scope.removeAllItem=function() {
        $scope.searchMap['category'] ='';
        $scope.searchMap['brand'] = '';
        delete $scope.searchMap.spec;//仅删除spec对象key对应的属性
        $scope.searchMap['price'] = '';
    }
    //构建分页标签(totalPages为总页数)
    buildPageLabel=function(){
        $scope.pageLabel=[];//新增分页栏属性
        var maxPageNo= $scope.resultMap.totalPages;//得到总页码
        var firstPage=1;//开始页码
        var lastPage=maxPageNo;//截止页码
        $scope.firstDot=true;//前面有点
        $scope.lastDot=true;//后边有点
        if(maxPageNo> 5){  //如果总页数大于5页,显示部分页码
            if($scope.searchMap.pageNo<=3){//如果当前页小于等于3
                lastPage=5; //前5页
                $scope.firstDot=false;//前面无点
            }else if( $scope.searchMap.pageNo>=lastPage-2  ){//如果当前页大于等于最大页码-2
                firstPage= maxPageNo-4;		 //后5页
                $scope.lastDot=false;//后边无点
            }else{ //显示当前页为中心的5页
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        }else{
            $scope.firstDot=false;//前面无点
            $scope.lastDot=false;//后边无点
        }
        //循环产生页码标签
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }
    //页码查询
    $scope.searchByPage=function (pageNo) {
        if (pageNo<1||pageNo>$scope.resultMap.totelPages)
        {
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }
    //判断当前页为第一页
    $scope.isTopPage=function(){
        if($scope.searchMap.pageNo==1){
            return true;
        }else{
            return false;
        }
    }
    //判断是否是尾页
    $scope.isEndPage=function(){
        if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    }
    //判断指定页码是否是当前页
    $scope.ispage=function (p) {
        if(parseInt(p)==parseInt($scope.searchMap.pageNo)){
            return true;
        }else {
            return false;
        }
    }
    //设置排序规则
    $scope.sortSearch=function(sortField,sort){
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }

    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function(){
        for(var i=0;i<$scope.resultMap.brandList.length;i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {//如果包含
                return true;
            }
        }
        return false;
    }
  });