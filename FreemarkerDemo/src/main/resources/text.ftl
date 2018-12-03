<html>
<head>
    <meta charset="UTF-8">
    <title>demo</title>
</head>
<body>
<#include "head.ftl">

<#--我是注释-->
${name},你好 ${message}</br>
<#--定义简单类型-->
<#assign linkname="周先生">
${linkname}</br>
<#if success=false>
通过实名认证
<#else>
未通过实名认证
</#if>
</br>

----商品列表----</br>
<#list goodsList as goods>
${goods_index}商品名称：${goods.name} 商品价格：${goods.price} </br>
</#list>
一共${goodsList?size}条记录<br>
<#--定义对象类型-->
<#assign text="{'bank':'工商银行','account':'123456'}">
<#--JSON字符串转对象-->
<#assign data=text?eval>
开户行：${data.bank}<br>
账号：${data.account}<br>
当前日期:${today?date}<br>
当前时间:${today?time}<br>
当前日期+时间:${today?datetime}<br>
日期格式化:${today?string("yyyy年MM月dd日 hh:mm:ss")}<br>
当前积分:${point?c}<br>
<#--判断变量是否存在-->
<#if aaa??>
  aaa变量存在
<#else>
  aaa变量不存在
</#if>

${aaa!'-'}

</body>
</html