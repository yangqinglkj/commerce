app.controller("searchController",function($scope,searchService,$location){
    //定义搜索对象的结构
    $scope.searchMap = {"keywords":"","category":"","brand":"","spec":{},"price":"","pageNum":1,"pageSize":10,"sort":"","sortField":""};


    //搜索
    $scope.search = function () {
        $scope.searchMap.pageNum = parseInt($scope.searchMap.pageNum);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;
                buildPageLabel();//构建分页
            }
        )
    };
    //构建分页
    buildPageLabel = function () {
        //构建分页栏
        $scope.pageLabel = [];
        var firstPage = 1;//开始页码
        var lastPage =$scope.resultMap.totalPages;//截止页码
        $scope.firstDot=true;//前面有省略号
        $scope.lastDot=true;//后面有省略号
        if ($scope.resultMap.totalPages > 5){//如果总页码大于5
            if ($scope.searchMap.pageNum <= 3){//如果当前页小于3,显示前5页
                lastPage = 5;
                $scope.firstDot = false;//前面没省略号
            }else if($scope.searchMap.pageNum >= $scope.resultMap.totalPages - 2){//显示后5页
                firstPage = $scope.resultMap.totalPages - 4;
                $scope.lastDot = false;//后面没省略号
            }else {//以当前页为中心的5页
                firstPage = $scope.searchMap.pageNum - 2;
                lastPage = $scope.searchMap.pageNum + 2;
            }
        }
        for (var i = firstPage; i <=lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };



     //添加搜索项，修改searchMap的值
    $scope.addSearchItem = function (key,value) {
        if (key == "category" || key == "brand" || key == "price"){//用户点击的是分类或品牌
            $scope.searchMap[key] = value;
        }else {//用户点击的是规格
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//查询
    };
    //撤销搜索项
    $scope.removeSearchItem = function (key) {
        if (key == "category" || key == "brand" || key == "price"){//用户点击的是分类或品牌
            $scope.searchMap[key] = "";
        }else {//用户点击的是规格
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.search();//查询
    };
    //分页查询
    $scope.queryByPage = function (pageNum) {
        if (pageNum < 1||pageNum >$scope.resultMap.totalPages){
            alert("输入的页码有误");
            return;
        }
        $scope.searchMap.pageNum = pageNum;
        $scope.search();//查询
    };
    //判断当前页是否为第一页
    $scope.isToPage = function () {
          if ($scope.searchMap.pageNum == 1){
              return true;
          }else {
              return false;
          }
    };
    //判断当前页是否为最后页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNum == $scope.resultMap.totalPages){
            return true;
        }else {
            return false;
        }
    };
    //排序
    $scope.sortSearch = function (sortField,sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.search();//查询
    };

    //判断关键字是否是品牌
    $scope.keywordsIsBrand=function(){
        for(var i=0;i< $scope.resultMap.brandList.length;i++){
            if( $scope.searchMap.keywords.indexOf( $scope.resultMap.brandList[i].text )>=0  ){
                return true;
            }
        }
        return false;
    };
    //获取传递过来的关键字
    $scope.loadKeywords = function () {
        $scope.searchMap.keywords = $location.search()['keywords']
        $scope.search();//查询
    };




});