//定义控制器
app.controller("brandController", function ($scope,brandService,$controller) {
    $controller("baseController",{$scope:$scope});

    //查询所有品牌
    $scope.findAll = function () {
        //通过$http接收后台数据
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            });
    };

    //分页方法
    $scope.findPage = function (page, size) {
        brandService.findPage(page, size).success(
            function (response) {
                $scope.list = response.rows;//显示当前页面数据
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        )
    };
    //添加品牌
    $scope.saveBrand = function () {
        var object = null;
        if ($scope.entity.id != null) {
            object = brandService.updateBrand($scope.entity);
        } else {
            object = brandService.addBrand($scope.entity);
        }
        object.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//成功了就刷新页面
                } else {
                    alert(response.message);//失败了就弹出错误提示框
                }
            }
        );
    };
    //根据id查询品牌
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    };

    //删除品牌
    $scope.dele = function () {
        brandService.dele($scope.selectIds).success(
            function (response) {
                if (response) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        )
    };
    //条件查询分页
    $scope.searchEntity = {};
    $scope.search = function (page, size) {
        brandService.search(page, size, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;//显示当前页面数据
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        )
    }


});