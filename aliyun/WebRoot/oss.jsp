<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>OSS文件管理</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="chrome=1,IE=edge"/>
    <meta name="renderer" content="webkit"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta content="yes" name="apple-mobile-web-app-capable">
    <meta content="yes" name="apple-touch-fullscreen">
    <meta content="telephone=no,email=no" name="format-detection">
    <meta name="msapplication-tap-highlight" content="no"/>
    <meta name="screen-orientation" content="portrait">
    <meta name="x5-orientation" content="portrait">
    <meta name="full-screen" content="yes">
    <meta name="x5-fullscreen" content="true">
    <meta name="browsermode" content="application">
    <meta name="x5-page-mode" content="app">
	<link rel="stylesheet" href="/css/bootstrap.min.css">
	<link rel="stylesheet" href="/css/bootstrap-theme.min.css">
	<link rel="stylesheet" href="/css/animate.css">
	<script src="/js/jquery.min.js"></script>
	<script src="/js/bootstrap.min.js"></script>
	<script src="/js/echarts.min.js"></script>
	<style type="text/css">
		.box {
			border: 1px solid #d9d9d9;
			border-radius: 4px;
			box-shadow: 0 2px 4px #d9d9d9;
			margin: 5px 0px 5px 10px;
			padding: 15px;
			padding-top: 30px;
			text-align: left;
			animation-duration: 1.2s;
  			-moz-animation-duration: 1.2s;
  			-webkit-animation-duration: 1.2s;
		}
	</style>
  </head>
  
  <body>
  	<jsp:include page="temp/nav.jsp"></jsp:include>
  	<div class="container" style="width:88%;margin-top:30px">
  		<div id="bucketBtnDiv">
  			<c:forEach items="${buckets}" var="bucket">
	  			<button inner="${bucket.intranetEndpoint}" outer="${bucket.extranetEndpoint}" location="${bucket.location}" create="<fmt:formatDate value="${bucket.creationDate}" pattern="yyyy-MM-dd HH:mm:ss" />" class="btn btn-default">${bucket.name}</button>
	  		</c:forEach>
  		</div>
  		<hr/>
  		<div id="bucketInfoDiv" class="box" style="margin: 5px 0px 5px 0px;display:none">
  		</div>
  		<div id="bucketFileDiv" class="box" style="margin: 5px 0px 5px 0px;display:none">
  			输入文件路径下载：<input id="filePathInput" type="text" class="form-control" style="width:400px;display:inline"/>
  			<button id="downloadBtn" class="btn btn-default" style="display:inline">下载</button>
  			<hr/>
  			<table id="bucketFileTable" class="table table-bordered" style="width:100%">
  			</table>
  		</div>
  	</div>
  </body>
  <script type="text/javascript">
  	var currentBucket="";
  	$("#bucketBtnDiv button").click(function(){
  		$("#bucketFileTable").html("");
  		 $("#bucketInfoDiv").show();
  		 currentBucket=$(this).html();
  		 $("#bucketBtnDiv button").each(function(){
  			 $(this).removeClass("active");
  		 });
  		 $(this).addClass("active");
  		 var html = $(this).html()+"的信息如下：<br/>";
  		 html += "内网访问地址："+$(this).attr("inner")+"<br/>";
  		 html += "外网访问地址："+$(this).attr("outer")+"<br/>";
  		 html += "服务器地址："+$(this).attr("location")+"<br/>";
  		 html += "创建时间："+$(this).attr("create");
  		 $("#bucketInfoDiv").html(html);
  		 $("#bucketFileDiv").show();
  	});
  	
  	$("#bucketBtnDiv button").dblclick(function(){
  		var btn = $(this);
  		$.ajax({
  			url:"/oss/rootFile",
  			data:"bucket="+btn.html(),
  			type:"POST",
  			success:function(data){
  				var html = "";
  				$.each(data,function(i,n){
  					html+="<tr><td>"+n+"</td></tr>";
  				});
  				$("#bucketFileTable").html(html);
  				$("#filePath").html("/");
  				$("#bucketFileTable tr").dblclick(getBucketFile);
  			}
  		});
 	});
  	function getBucketFile(){
  		$("#filePathInput").val($(this).text());
  	}
  	$("#downloadBtn").click(function(){
  		var filePath = $("#filePathInput").val();
  		location.href="/oss/download?bucket="+currentBucket+"&filePath="+filePath;
  	});
  </script>
</html>
