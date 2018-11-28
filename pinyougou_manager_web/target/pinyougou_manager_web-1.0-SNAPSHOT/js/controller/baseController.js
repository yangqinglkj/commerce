app.controller("baseController", function ($scope) {
    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1, //当前页
        totalItems: 10, //总记录数
        itemsPerPage: 10, //每页记录数
        perPageOptions: [10, 20, 30, 40, 50], //分页选项
        onChange: function () { // 当分页选项页码改变后触发的方法
            $scope.reloadList();
        }
    };
    //刷新列表
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage)

    };

    //用户勾选复选框
    $scope.selectIds = [];//用户勾选的id集合
    $scope.updateBrand = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);//push向集合里添加id
        } else {
            var index = $scope.selectIds.indexOf(id);//查找id所在的索引位置
            $scope.selectIds.splice(index, 1);//移除的位置,移除的个数
        }
    };

    //提取json字符串数据中某个属性，返回拼接字符串 逗号分隔
    $scope.jsonToString = function (jsonString, key) {
        //转换为Json对象或者集合
        var json = JSON.parse(jsonString);
        var value = "";

        for (var i = 0; i < json.length; i++) {
            if (i > 0) {
                value += ",";
            }
            value += json[i][key];
        }
        return value;
    }

});