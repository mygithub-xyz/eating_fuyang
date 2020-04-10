app.service('brandService',function ($http) {

    this.findAll=function () {
        return $http.get('../brand/findAll.do');
    }
    this.findPage=function (page, rows) {
        return $http.get('../brand/findPage.do?page='+page+"&rows="+rows);
    }
    this.add=function (brand) {
        return $http.post("../brand/add.do", brand);
    }
    this.update=function (brand) {
        return $http.post("../brand/update.do", brand);
    }

    this.findOne=function (id) {
        return $http.get('../brand/findOne.do?id='+id);
    }

    this.dele=function (selectIds) {
        return $http.get('../brand/dele.do?ids='+selectIds);
    }

    this.search=function (page, rows, searchEntity) {
        return $http.post('../brand/search.do?page='+page+"&rows="+rows, searchEntity);
    }
});