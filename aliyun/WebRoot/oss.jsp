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
  		<div id="bucketBtnDiv">
  			<c:forEach items="${buckets}" var="bucket">
	  			<button inner="${bucket.intranetEndpoint}" outer="${bucket.extranetEndpoint}" location="${bucket.location}" create="<fmt:formatDate value="${bucket.creationDate}" pattern="yyyy-MM-dd HH:mm:ss" />" class="btn btn-default">${bucket.name}</button>
	  		</c:forEach>
  		</div>
  		<hr/>
  		<div id="bucketInfoDiv" class="box" style="margin: 5px 0px 5px 0px;display:none">
  		</div>
  		<div id="bucketFileDiv" class="box" style="margin: 5px 0px 5px 0px;display:none">
  			文件路径：
  			<button id="backBtn" class="btn btn-default">&lt;</button>
  			<input id="filePathInput" type="text" class="form-control" style="width:400px;display:inline;margin-top:3px"/>
  			<button id="jumpBtn" class="btn btn-default" style="display:inline">跳转</button>
  			<button id="downloadBarBtn" class="btn btn-default" style="display:inline">下载</button>
  			<button id="uploadFileBtn" class="btn btn-default" style="display:inline">上传</button>
  			<input id="ossUpFile" name="ossUpFile" type="file" style="display:none">
  			<hr/>
  			<table id="bucketFileTable" class="table table-bordered" style="width:100%">
  			</table>
  		</div>
  	</div>
  </body>
  <script type="text/javascript">
  	var prefix = "";
  	var current = "";
  	var currentBucket="";
  	$("#bucketBtnDiv button").click(function(){
  		prefix = "";
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
  		getFileList(btn.html(),prefix,true);
 	});
  	function downloadFile(type,currentName){
  		location.href="/oss/download?bucket="+currentBucket+"&filePath="+currentName+"&type="+type;
  	}
  	function deleteFile(type,currentName){
  		$.ajax({
  			type:"POST",
  			data:"bucket="+currentBucket+"&filePath="+currentName+"&type="+type,
  			url:"/oss/deleteFile",
  			beforeSend:function(){
  				$("#bk").show();	
  			},
  			success:function(data){
  				$("#bk").hide();	
  				if("success" == data){
  					getFileList(currentBucket,prefix,false);
  				}
  			}
  		});
  	}
  	function checkPath(path){
  		$.ajax({
  			url:"/oss/checkPath",
  			type:"POST",
  			async:false,
  			data:"path="+path+"&bucket="+currentBucket,
  			beforeSend:function(){
  				$("#bk").show();	
  			},
  			success:function(data){
  				$("#bk").hide();
  				if("|not file|"==data){
  					alert($("#filePathInput").val()+"文件不存在");
  				}else{
  					$("#filePathInput").val(data);
  				}
  			}
  		});
  	}
  	$("#jumpBtn").click(function(){
  		var val = $("#filePathInput").val();
  		checkPath(val);
  		//开始跳转
  		prefix = $("#filePathInput").val();
  		getFileList(currentBucket,prefix,true);
  	});
  	$("#backBtn").click(function(){
  		var val = $("#filePathInput").val();
  		checkPath(val);
  		prefix = $("#filePathInput").val();
  		if(prefix.indexOf("/") == -1){
  			prefix = "";
  		}else{
  			prefix = prefix.substr(0,prefix.lastIndexOf("/"));
  		}
  		getFileList(currentBucket,prefix,true);
  	});
  	$("#downloadBarBtn").click(function(){
  		var val = $("#filePathInput").val();
  		checkPath(val);
  		//开始下载
  		prefix = $("#filePathInput").val();
  		getFileList(currentBucket,prefix,false);
  		downloadFile(1,prefix);
  	});
  	function getFileList(bucket,prefix,asyncType){
  		$("#filePathInput").val(prefix);
  		$.ajax({
  			url:"/oss/rootFile",
  			data:"bucket="+bucket+"&prefix="+prefix,
  			async:asyncType,
  			type:"POST",
  			beforeSend:function(){
  				$("#bk").show();	
  			},
  			success:function(data){
  				$("#bk").hide();
  				var html = "";
  				$.each(data,function(i,n){
  					if(n.type == 1){
	  					html+="<tr type='1'><td class='fileName' colspan='3'>"+n.fileName+"</td><td><a href='javascript:;' onclick=\"downloadFile('"+n.type+"','"+n.currentName+"')\">下载</a> | <a href='javascript:;' onclick=\"deleteFile('"+n.type+"','"+n.currentName+"')\">删除</a></td></tr>";
  					}else{
  						html+="<tr type='0'><td style='color:#ababab'>"+n.fileName+"</td><td><code>"+n.size+"</code></td><td><code>"+n.modifyTime+"</code></td><td><a href='javascript:;' onclick=\"downloadFile('"+n.type+"','"+n.currentName+"')\">下载</a> | <a href='javascript:;' onclick=\"deleteFile('"+n.type+"','"+n.currentName+"')\">删除</a></td></tr>";
  					}
  				});
  				$("#bucketFileTable").html(html);
  				//文件夹点击
  				$("#bucketFileTable tr[type='1']").dblclick(function(){
  					if(prefix == ""){
  						prefix = $(this).find(".fileName").text();
  					}else{
  						prefix = prefix + "/" +$(this).find(".fileName").text();
  					}
  					getFileList(bucket,prefix,true);
  				});
  				//文件点击
  			}
  		});
  	}
  	$("#uploadFileBtn").click(function(){
  		$("#ossUpFile").click();
  	});
  	$("#ossUpFile").change(uploadFileFunc);
  	function uploadFileFunc(){
  		prefix = $("#filePathInput").val();
  		$.ajaxFileUpload({
  		     url:'/oss/uploadFileAjax',
  		     fileElementId:'ossUpFile',
  		   	 dataType : 'text',
  		   	 data:{"bucket":currentBucket,"prefix":prefix},
  		  	 beforeSend:function(){
 				$("#bk").show();	
 			},
  		     success: function (data, status){
  		    		$("#bk").hide();
  		    		getFileList(currentBucket,prefix,false);
  		    		$("#ossUpFile").val("");
  		    		$("#ossUpFile").change(uploadFileFunc);
  		     },
  		     error: function (data, status){
  		    		$("#bk").hide();
  		    		$("#ossUpFile").val("");
  		    		$("#ossUpFile").change(uploadFileFunc);
  		    		//错误处理
  		     }
  		 });
  	}
  </script>
</html>