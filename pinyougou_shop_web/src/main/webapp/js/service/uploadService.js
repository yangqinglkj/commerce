app.service("uploadService",function ($http) {
    //上传文件
    this.uploadFile = function () {
        var formData = new FormData();
        formData.append("file",file.files[0]);//file 代表文件上传框的name
        return $http({
            url:"../upload.do",
            method:"post",
            data:formData,
            //默认类型是json,如果是上传图片就要指定undefined
            headers:{"Content-Type" :undefined},
            //对表单进行二进制序列化
            transformRequest:angular.identity
        });
    }
});