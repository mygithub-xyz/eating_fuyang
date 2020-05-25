//服务层
app.service('seckillGoodsService',function($http){
    //显示登录人名字
    this.showName=function(){
        return  $http.get('../login/name.do');
    }
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../seckillGoods/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../seckillGoods/findPage.do?page='+page+'&rows='+rows);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../seckillGoods/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../seckillGoods/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../seckillGoods/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../seckillGoods/search.do?page='+page+"&rows="+rows, searchEntity);
	}
	//查询当前秒杀商品列表
	this.findList=function(){
		return $http.get('../seckillGoods/findList.do');
	}
    //读取列表数据绑定到表单中
    this.findList=function(){
        return $http.get('seckillGoods/findList.do');
    }
    //查询redis中秒杀商品
    this.findOne=function(id){
        return $http.get('seckillGoods/findOneFromRedis.do?id='+id);
    }
    //提交订单
    this.submitOrder=function(seckillId){
        return $http.get('seckillOrder/submitOrder.do?seckillId='+seckillId);
    }
});