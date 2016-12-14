<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>aliyun备份文件</title>
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
  		<div class="btn-group">
	  		<button id="getUpdateInfoBtn" class="btn btn-default">备份信息</button>
	  		<button id="getRestartInfoBtn" class="btn btn-default">重启信息</button>
	  	</div>
	  		<button id="getFileTableBtn" class="btn btn-default">备份列表</button>
	  	<div class="btn-group">
	  		<button id="updateBakBtn" class="btn btn-primary">开始备份</button>
	  		<button id="restartTomcatBtn" class="btn btn-danger">开始重启</button>
	  	</div>
  		<div class="btn-group">
		  <button type="button" class="btn" id="autoBakBtn"></button>
		  <button type="button" class="btn" id="autoRestartBtn"></button>
		</div>
  		<hr/>
  		<div id="getFileTableDiv"></div>
  		<div id="getUpdateInfoDiv"></div>
  	</div>
  </body>
  <script type="text/javascript">
  	$("#getFileTableBtn").click(function(){
  		$.ajax({
  			url:"/bak/getFileTable",
  			type:"POST",
  			success:function(data){
  				var content = "<table class=\"table table-bordered\" style=\"width:100%\">";
  				$.each(data,function(i,n){
  					content += "<tr><td>"+n+"</td></tr>";
  				});
  				content += "</table>";
  				$("#getFileTableDiv").html(content);
  			}
  		});
  	});
  	$("#getUpdateInfoBtn").click(function(){
  		$.ajax({
  			url:"/bak/getUpdateInfo",
  			type:"POST",
  			success:function(data){
  				$("#getUpdateInfoDiv").html(data);
  			}
  		});
  	});
  	$("#getRestartInfoBtn").click(function(){
  		$.ajax({
  			url:"/bak/getRestartInfo",
  			type:"POST",
  			success:function(data){
  				$("#getUpdateInfoDiv").html(data);
  			}
  		});
  	});
  	$("#updateBakBtn").click(function(){
  		var btn = $(this); 
  		if(btn.attr("class").indexOf("btn-primary")!=-1){
  			$.ajax({
  	  			url:"/bak/uploadBak",
  	  			type:"POST",
  	  			beforeSend:function(){
  	  				btn.removeClass("btn-primary").addClass("btn-warning");
  	  				btn.html("备 份 中");
  	  			},
  	  			success:function(data){
  	  				btn.removeClass("btn-warning").addClass("btn-primary");
	  				btn.html("开始备份");
  	  				$("#getUpdateInfoBtn").click();
  	  			}
  	  		});
  		}
  	});
  	function getRestartInfo(){
  		$.ajax({
  			url:"/bak/getRestartInfo",
  			type:"POST",
  			error:function(){
  				setTimeout("getRestartInfo()",1000);
  			},
  			success:function(data){
  				if(""!=data){
  					$("#restartTomcatBtn").html("开始重启");
  					$("#getUpdateInfoDiv").html(data);
  				}else{
  					setTimeout("getRestartInfo()",1000);
  				}
  			}
  		});
  	}
  	$("#restartTomcatBtn").click(function (){
  		var btn = $(this); 
  		if(btn.html()=="开始重启"){
  			$.ajax({
  	  			url:"/bak/restartTomcat",
  	  			type:"POST",
  	  			beforeSend:function(){
  	  				$("#restartTomcatBtn").html("重 启 中");
  	  			},
  	  			success:function(data){
  	  				getRestartInfo();
  	  			}
  	  		});
  		}
  	});
  	$("#autoBakBtn").click(function(){
  		changeRule(0);
  	});
  	$("#autoRestartBtn").click(function(){
  		changeRule(1);
  	});
  	function changeRule(type){
  		$.ajax({
  			type:"POST",
  			url:"/bak/changeRule",
  			data:"type="+type,
  			success:function(data){
  				initRule(data);
  			}
  		});
  	}
  	initRule("${rule}");
  	function initRule(rule){
  		var rs = rule.split("|");
  		if(rs[0] == "1"){
  			$("#autoBakBtn").html("关闭自动备份");
  			$("#autoBakBtn").removeClass("btn-info").addClass("btn-danger");
  		}else{
  			$("#autoBakBtn").html("开启自动备份");
  			$("#autoBakBtn").removeClass("btn-danger").addClass("btn-info");
  		}
  		if(rs[1] == "1"){
  			$("#autoRestartBtn").html("关闭自动重启");
  			$("#autoRestartBtn").removeClass("btn-info").addClass("btn-danger");
  		}else{
  			$("#autoRestartBtn").html("开启自动重启");
  			$("#autoRestartBtn").removeClass("btn-danger").addClass("btn-info");
  		}
  	}
  </script>
</html>
