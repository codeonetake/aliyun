<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>数据统计</title>
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
	<script src="/js/echarts.min.js"></script>
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
  	<input style="display:none" type="file" class="moneyPic" name="moneyPic" id="addMoneyPic"/>
  	<div class="container" style="width:88%;margin-top:30px">
  		<button id="addMoneyPicBtn" class="btn btn-info" style="width:100%">上传图片</button>
  		<div class="alert alert-${color}" role="alert">
  			${status}
  		</div>
  		<hr/>
  		<div id="upMoneyTongji" style="width:100%;height:350px"></div>
  		<div id="baseMoneyTongji" style="width:100%;height:350px"></div>
  		<hr/>
  		截止到${firstTime}的总收益为：<b>${totalUp}</b>
  		<table style="width:100%" class="table table-bordered">
  			<tr>
	  			<td>名称</td>
	  			<td>时间</td>
	  			<td>金额</td>
	  			<td>时间</td>
	  			<td>金额</td>
	  			<td>差值</td>
  			</tr>
  			<tr>
	  			<td>收益值</td>
	  			<td>${upYesterday.time}</td>
	  			<td>${upYesterday.money}</td>
	  			<td>${upToday.time}</td>
	  			<td>${upToday.money}</td>
	  			<c:if test="${upYesterday.money > upToday.money}">
	  				<td style="color:red"><fmt:formatNumber value="${upYesterday.money - upToday.money}" pattern="#0.00"/></td>
	  			</c:if>
	  			<c:if test="${upYesterday.money < upToday.money}">
	  				<td style="color:green"><fmt:formatNumber value="${upToday.money - upYesterday.money}" pattern="#0.00"/></td>
	  			</c:if>
	  			<c:if test="${upYesterday.money == upToday.money}">
	  				<td><fmt:formatNumber value="${upYesterday.money - upToday.money}" pattern="#0.00"/></td>
	  			</c:if>
  			</tr>
  			<tr>
	  			<td>总资产</td>
	  			<td>${baseYesterday.time}</td>
	  			<td>${baseYesterday.money}</td>
	  			<td>${baseToday.time}</td>
	  			<td>${baseToday.money}</td>
	  			<c:if test="${baseYesterday.money > baseToday.money}">
	  				<td style="color:red"><fmt:formatNumber value="${baseYesterday.money - baseToday.money}" pattern="#0.00"/></td>
	  			</c:if>
	  			<c:if test="${baseYesterday.money < baseToday.money}">
	  				<td style="color:green"><fmt:formatNumber value="${baseToday.money - baseYesterday.money}" pattern="#0.00"/></td>
	  			</c:if>
	  			<c:if test="${baseYesterday.money == baseToday.money}">
	  				<td><fmt:formatNumber value="${baseYesterday.money - baseToday.money}" pattern="#0.00"/></td>
	  			</c:if>
  			</tr>
  		</table>
  	</div>
  </body>
  <script type="text/javascript">
  	$("#addMoneyPicBtn").click(function(){
		$("#addMoneyPic").click();
	});
	$("#addMoneyPic").change(changeWeixinPic);
	function changeWeixinPic(){
		$.ajaxFileUpload({
		     url:'/tongji/uploadFileAjax',
		     fileElementId:'addMoneyPic',
		   	 dataType : 'text',
		     success: function (data, status){
		    	 	$("#addMoneyPic").change(changeWeixinPic);
		     },
		     error: function (data, status){
		    		//错误处理
		    		$("#addMoneyPic").change(changeWeixinPic);
		     }
		 });
	}
  </script>
  <c:if test="${noUp == '0'}">
  <script type="text/javascript">
	var option = {
	    title: {
	        text: '收益值',
	        subtext: ''
	    },
	    tooltip: {
	        trigger: 'axis'
	    },
	    legend: {
	        data:['收益值']
	    },
	    grid: {
	        left: '3%',
	        right: '4%',
	        bottom: '3%',
	        containLabel: true
	    },
	    toolbox: {
	        feature: {
	            saveAsImage: {}
	        }
	    },
	    xAxis: {
	        type: 'category',
	        boundaryGap: false,
	        data: ${upTime}
	    },
	    yAxis: {
	        type: 'value'
	    },
	    series: [
	        {
	            name:'收益值',
	            type:'line',
	            data:${upMoney}
	        }
	    ]
	};
	echarts.init(document.getElementById("upMoneyTongji")).setOption(option);
  </script>
  </c:if>
  <c:if test="${noBase == '0'}">
  <script type="text/javascript">
  	option = {
	    title: {
	        text: '总资产',
	        subtext: ''
	    },
	    tooltip: {
	        trigger: 'axis'
	    },
	    legend: {
	        data:['总资产']
	    },
	    grid: {
	        left: '3%',
	        right: '4%',
	        bottom: '3%',
	        containLabel: true
	    },
	    toolbox: {
	        feature: {
	            saveAsImage: {}
	        }
	    },
	    xAxis: {
	        type: 'category',
	        boundaryGap: false,
	        data: ${baseTime}
	    },
	    yAxis: {
	        type: 'value'
	    },
	    series: [
	        {
	            name:'总资产',
	            type:'line',
	            data:${baseMoney}
	        }
	    ]
	};
	echarts.init(document.getElementById("baseMoneyTongji")).setOption(option);
  </script>
  </c:if>
</html>