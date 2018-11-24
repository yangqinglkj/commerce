//品牌服务层
app.service("brandService", function ($http) {
    this.findAll = function () {
        return $http.get("../brand/findAll.do");
    };
    this.findPage = function (page, rows) {
        return $http.get("../brand/findPage.do?page=" + page + "&size=" + rows);
    };
    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id=" + id);
    };
    this.addBrand = function (entity) {
        return $http.post("../brand/addBrand.do", entity)
    };
    this.updateBrand = function (entity) {
        return $http.post("../brand/updateBrand.do", entity)
    };
    this.dele = function (ids) {
        return $http.get("../brand/deleteBrand.do?ids=" + ids)
    };
    this.search = function (page, size, searchEntity) {
        return $http.post("../brand/search.do?page=" + page + "&size=" + size, searchEntity)
    };
    //下拉列表
    this.selectOptionList = function () {
        return $http.get("../brand/selectOptionList.do");
    };

});