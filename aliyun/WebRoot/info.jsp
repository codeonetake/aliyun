<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>信息查询</title>
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
  		<c:forEach items="${content}" var="c">
  			<div class="panel panel-primary">
			  <div class="panel-heading">${c.key}</div>
			  <div class="panel-body" style="word-break: break-all;">
			    ${c.value}
			  </div>
			</div>
  		</c:forEach>
  	</div>
  </body>
  <script src="/js/fileUpload.js"></script>
  <script type="text/javascript">
  	$("#addFestivalBtn").click(function(){
  		$(".weixinPic").val("");
  		$("#addDate").val("");
  		$("#addName").val("");
  		$("#addBackColor").val("");
  		$("#addPicId").val("");
  		$("#addWeixinPic").val("");
  		$("#addBackColorTd").css("backgroundColor","#fff");
  		$("#addModal").modal("show");
  	});
  	$("#addBackColor").keyup(function(){
  		var val = $(this).val();
  		if(val.length == 6){
  			$("#addBackColorTd").css("backgroundColor","#"+val);
  		}else{
  			$("#addBackColorTd").css("backgroundColor","#fff");
  		}
  	});
  	$("#addForm").click(function(){
  		$.ajax({
  			url:"/festival/add",
  			data:$("#addFestivalForm").serialize(),
  			type:"POST",
  			success:function(data){
  				alert(data);
  				location.reload();
  			}
  		});
  	});
  	
  	function changeFestival(id){
  		$("#changeDate").val($("#id"+id+" .date").html());
  		$("#changeName").val($("#id"+id+" .name").html());
  		$("#changeBackColor").val($("#id"+id+" .backColor").html());
  		$("#changePicId").val($("#id"+id+" .picId").html());
  		$("#changeWeixinPic").val($("#id"+id+" .weixinPic").html());
  		$("#changeBackColorTd").css("backgroundColor","#"+$("#id"+id+" .backColor").html());
  		$("#changeFestivalId").val(id);
  		$(".weixinPic").val("");
  		$("#changeModal").modal("show");
  	}
  	
  	function delFestival(id){
  		$.ajax({
  			url:"/festival/del",
  			data:"id="+id,
  			type:"POST",
  			success:function(data){
  				alert(data);
  				location.reload();
  			}
  		});
  	}
  	
  	function exeFestival(id){
  		$.ajax({
  			url:"/festival/exec",
  			data:"id="+id,
  			type:"POST",
  			success:function(data){
  				alert(data);
  			}
  		});
  	}
  	
  	$("#changeForm").click(function(){
  		$.ajax({
  			url:"/festival/change",
  			data:$("#changeFestivalForm").serialize(),
  			type:"POST",
  			success:function(data){
  				alert(data);
  				location.reload();
  			}
  		});
  	});
  	
  	$("#changeBackColor").keyup(function(){
  		var val = $(this).val();
  		if(val.length == 6){
  			$("#changeBackColorTd").css("backgroundColor","#"+val);
  		}else{
  			$("#changeBackColorTd").css("backgroundColor","#fff");
  		}
  	});
  	
  	$("#defaultBtn").click(function(){
  		$.ajax({
  			url:"/festival/defaultTheme",
  			type:"POST",
  			success:function(data){
  				alert(data);
  			}
  		});
  	});
  	$("#addWeixinPicBtn").click(function(){
  		$("#addWeixinPic").click();
  	});
  	$("#addWeixinPic").change(changeWeixinPic);
  	function changeWeixinPic(){
  		$.ajaxFileUpload({
 		     url:'/festival/uploadFileAjax',
 		     fileElementId:'addWeixinPic',
 		   	 dataType : 'text',
 		     success: function (data, status){
 		    	 	$("#uploadFileResult").html(data);
 		    	 	alert($("#uploadFileResult").text());
 		    	 	$("#addWeixinPic").change(changeWeixinPic);
 		     },
 		     error: function (data, status){
 		    		//错误处理
 		    		$("#addWeixinPic").change(changeWeixinPic);
 		     }
 		 });
 	}
  </script>
</html>