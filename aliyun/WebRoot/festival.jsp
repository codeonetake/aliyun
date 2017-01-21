<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>节日管理</title>
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
  		<button id="addFestivalBtn" class="btn btn-primary">添加节假日</button>
  		<button id="defaultBtn" class="btn btn-default">设置默认主题</button>
  		<a href="http://codeawl.com" target="_blank" class="btn btn-info">查看效果</a>
  		<hr/>
  		<table class="table table-bordered" style="width:100%">
  			<tr>
  				<td>id</td>
  				<td>日期</td>
  				<td>名称</td>
  				<td>背景颜色</td>
  				<td>头像ID</td>
  				<%--<td>二维码地址</td>
  				--%><td>创建时间</td>
  				<td>修改时间</td>
  				<td>操作</td>
  			</tr>
  			<c:forEach items="${festivals}" var="festival">
  			<tr id="id${festival.id}">
  				<td class="id">${festival.id}</td>
  				<td class="date">${festival.date}</td>
  				<td class="name">${festival.name}</td>
  				<td class="backColor" style="background-color: #${festival.backColor}">${festival.backColor}</td>
  				<td class="picId">${festival.picId}</td>
  				<%--<td class="weixinPic">${festival.weixinPic}</td>
  				--%><td class="createTime">${festival.createTime}</td>
  				<td class="modifyTime">${festival.modifyTime}</td>
  				<td><a href="javascript:;" onclick="changeFestival('${festival.id}')">修改</a> | <a href="javascript:;" onclick="delFestival('${festival.id}')">删除</a>  | <a href="javascript:;" onclick="exeFestival('${festival.id}')">执行</a> </td>
  			</tr>
  			</c:forEach>
  		</table>
  	</div>
  	
  	<div id="addModal" class="modal fade">
	  <div class="modal-dialog" style="width:50%">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title">添加节假日</h4>
	      </div>
	      <div class="modal-body">
	        <p>
	        		<form id="addFestivalForm">
	        		<table class="table table-bordered" style="width:100%">
	        			<tr>
	        				<td>日期</td>
	        				<td><input id="addDate" name="addDate" class="form-control"/></td>
	        			</tr>
	        			<tr>
	        				<td>名称</td>
	        				<td><input id="addName" name="addName" class="form-control"/></td>
	        			</tr>
	        			<tr>
	        				<td>背景颜色</td>
	        				<td id="addBackColorTd"><input id="addBackColor" name="addBackColor" class="form-control"/></td>
	        			</tr>
	        			<tr>
	        				<td>头像ID</td>
	        				<td><input id="addPicId" name="addPicId" class="form-control"/></td>
	        			</tr>
	        			<%--<tr>
	        				<td>二维码图片</td>
	        				<td><input id="addWeixinPic" name="addWeixinPic" class="form-control"/></td>
	        			</tr>
	        		--%></table>
	        		</form>
	        </p>
	      </div>
	      <div class="modal-footer">
	      	<button id="addForm" type="button" class="btn btn-primary">添加</button>
	        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	      </div>
	    </div><!-- /.modal-content -->
	 </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
	
	<div id="changeModal" class="modal fade">
	  <div class="modal-dialog" style="width:50%">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title">修改节假日</h4>
	      </div>
	      <div class="modal-body">
	        <p>
	        		<form id="changeFestivalForm">
	        		<input id="changeFestivalId" type="hidden" name="changeId"/>
	        		<table class="table table-bordered" style="width:100%">
	        			<tr>
	        				<td>日期</td>
	        				<td><input id="changeDate" name="changeDate" class="form-control"/></td>
	        			</tr>
	        			<tr>
	        				<td>名称</td>
	        				<td><input id="changeName" name="changeName" class="form-control"/></td>
	        			</tr>
	        			<tr>
	        				<td>背景颜色</td>
	        				<td id="changeBackColorTd"><input id="changeBackColor" name="changeBackColor" class="form-control"/></td>
	        			</tr>
	        			<tr>
	        				<td>头像ID</td>
	        				<td><input id="changePicId" name="changePicId" class="form-control"/></td>
	        			</tr>
	        			<%--<tr>
	        				<td>二维码图片</td>
	        				<td><input id="changeWeixinPic" name="changeWeixinPic" class="form-control"/></td>
	        			</tr>
	        		--%></table>
	        		</form>
	        </p>
	      </div>
	      <div class="modal-footer">
	      	<button id="changeForm" type="button" class="btn btn-primary">修改</button>
	        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	      </div>
	    </div><!-- /.modal-content -->
	 </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
  	<div id="bk" style="width:100%;height:100%;position:absolute;top:0px;left:0px;overflow:hidden;z-index:999;background-color:black;display:none"></div>
  </body>
  <script type="text/javascript">
  	$("#addFestivalBtn").click(function(){
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
  </script>
</html>