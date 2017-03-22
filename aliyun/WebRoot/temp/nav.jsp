<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
  <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
        <span class="sr-only"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="javascript:;">Codeawl</a>
    </div>
    <input style="display:none" type="file" class="moneyNavPic" name="moneyNavPic" id="addMoneyNavPic"/>
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <ul class="nav navbar-nav">
      	<li id="uploadMoneyLi" style="display:none"><a id="uploadMoneyBtn" href="javascript:;">上传资产图片</a></li>
        <li><a href="/oss">OSS集群</a></li>
        <li><a href="/bak">服务器备份</a></li>
        <li><a href="/cdn">CDN管理</a></li>
        <li><a href="/festival">节日管理</a></li>
        <li><a href="/info">信息查询</a></li>
        <li class="dropdown">
          <a href="javascript" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">数据统计<span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="/tongji/cdn">CDN统计</a></li>
            <li><a href="/tongji/baidu">百度统计</a></li>
            <li><a href="/tongji/system">系统统计</a></li>
            <li><a href="/tongji/log">日志统计</a></li>
            <%--<li><a href="/tongji/article">文章测速</a></li>
            --%><li><a href="/tongji/money">财务统计</a></li>
          </ul>
        </li>
      </ul>
    </div>
  </div>
</nav>
<script type="text/javascript">
var userAgentInfo = navigator.userAgent;
if(userAgentInfo.indexOf("iPhone")!=-1){
	$("#uploadMoneyLi").show();
}else{
	$("#uploadMoneyLi").hide();
}
$("#uploadMoneyBtn").click(function(){
	$("#addMoneyNavPic").click();
});
$("#addMoneyNavPic").change(changeWeixinPic);
function changeWeixinPic(){
	$.ajaxFileUpload({
	     url:'/tongji/uploadFileNavAjax',
	     fileElementId:'addMoneyNavPic',
	   	 dataType : 'text',
	     success: function (data, status){
	    	 	$("#addMoneyNavPic").change(changeWeixinPic);
	     },
	     error: function (data, status){
	    		//错误处理
	    		$("#addMoneyNavPic").change(changeWeixinPic);
	     }
	 });
}
</script>