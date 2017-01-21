<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>cdn管理</title>
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
	<link rel="stylesheet" href="/css/animate.css">
	<script src="/js/jquery.min.js"></script>
	<script src="/js/bootstrap.min.js"></script>
	<script src="/js/fileUpload.js"></script>
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
		#bk{
		     filter:alpha(opacity=75);
		    /*IE滤镜，透明度50%*/
		     -moz-opacity:0.75;
		    /*Firefox私有，透明度50%*/
		     opacity:0.75;
		    /*其他，透明度50%*/
		}
	</style>
  </head>
  
  <body>
  	<jsp:include page="temp/nav.jsp"></jsp:include>
  	<div class="container" style="width:88%;margin-top:30px">
  		<div class="col-lg-6">
	    <div class="input-group">
	      <input id="testUrl" type="text" class="form-control">
	      <span class="input-group-btn">
	        <button id="testCdnBtn" class="btn btn-default" type="button">检测</button>
	        <button id="msgBtn" class="btn btn-primary" type="button">刷新消息</button>
	      </span>
	    </div>
	  </div><br/>
	  <div id="cdnResult" style="width:100%"></div>
	  <div id="cdnMsgDiv" style="width:100%"></div>
  	</div>
  	<div id="bk" style="width:100%;height:100%;position:absolute;top:0px;left:0px;overflow:hidden;z-index:999;background-color:black;display:none"></div>
  </body>
  <script type="text/javascript">
  	$("#testCdnBtn").click(function(){
  		$.ajax({
  			url:"/cdn/getType",
  			data:"url="+$("#testUrl").val(),
  			type:"POST",
  			beforeSend:function(){
  				$("#bk").show();
  				$("#cdnResult").html("");
  			},
  			success:function(data){
  				$("#bk").hide();
  				var html = "<br/><hr/><table class=\"table table-bordered\" style=\"width:100%\">";
  				$.each(data,function(url,type){
  					var color="green";
  					if("HIT" != type){
  						color="red";
  					}
  					html += "<tr><td>"+url+"</td><td style='color:"+color+"'><b>"+type+"</b></td></tr>";
  				});
  				html+="</table>";
  				$("#cdnResult").html(html);
  			}
  		});
  	});
  	$("#msgBtn").click(function(){
  		$.ajax({
  			url:"/cdn/getMsg",
  			type:"POST",
  			success:function(data){
  				$("#cdnMsgDiv").html("<hr/>"+data);
  			}
  		});
  	});
  </script>
</html>