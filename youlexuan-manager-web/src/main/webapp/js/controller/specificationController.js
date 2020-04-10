 //控制层 
app.controller('specificationController' ,function($scope,$controller   ,specificationService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		specificationService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		specificationService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		specificationService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){
		var serviceObject;//服务层对象
		if($scope.entity.specification.id!=null){//如果有ID
			serviceObject=specificationService.update( $scope.entity ); //修改
		}else{
            if ($scope.entity.specification.option_name!=null&&$scope.entity.specificationOptionList.length>0){
                serviceObject=specificationService.add( $scope.entity  );//增加
        }else{
            alert("请先填写规格信息，再添加！")
        }
		}
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框
  if ($scope.selectIds.length>0){
      specificationService.dele( $scope.selectIds ).success(
          function(response){
              if(response.success){
                  $scope.reloadList();//刷新列表
                  $scope.selectIds=[];
                  alert(response.message)
              }
          }
      );
  }else {
      alert("删除失败，请先选择吧！")
  }
	}

	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		specificationService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //初始化一个前端的规格的组合实体类的js的json的对象,前端的一对多
    //改对象什么时候发生变化:增加(点击"新增规格选项"),删除(点击"删除"对应的索引的行)
    //前台的组合实体类
    $scope.entity={specification:{},specificationOptionList:[{}]};

    //增加规格项
    $scope.addTableRow=function () {
        $scope.entity.specificationOptionList.push({});
    }

    //删除规格项
    $scope.deleTableRow=function (index) {
        if ( $scope.entity.specificationOptionList.length>1){
            $scope.entity.specificationOptionList.splice(index, 1);
        }
    }
});
