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
  		<div id="baiduTongji" style="width:100%;height:350px"></div>
  		<hr/>
  		<code>更新时间：${baiduSpider.time}</code><br/><br/>
  		<div class="row">
  		<div class="col-md-6" id="spiderPan" style="width:50%;height:350px"></div>
  		<div class="col-md-6">
  			
  		</div>
  		</div>
  		<table class="table table-bordered" style="width:100%">
			<tr>
				<td>ip</td>
				<td>时间</td>
				<td>访问的url</td>
				<td>状态码</td>
			</tr>
 			<c:forEach items="${baiduSpider.infoDetails}" var="info">
 			<tr>
				<td>${info.ip}</td>
				<td>${info.time}</td>
				<td>${info.url}</td>
				<td>${info.responseCode}</td>
			</tr>
 			</c:forEach>
 		</table>
 		<c:forEach items="${baiduSpider.originInfo}" var="info">
 			<div class="alert alert-info" role="alert">${info}</div>
 		</c:forEach>
  	</div>
  </body>
  <script type="text/javascript">
  	var option = {
	    title: {
	        text: '百度收录数量',
	        subtext: ''
	    },
	    tooltip: {
	        trigger: 'axis'
	    },
	    legend: {
	        data:['收入量']
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
	        data: ${time}
	    },
	    yAxis: {
	        type: 'value'
	    },
	    series: [
	        {
	            name:'收入量',
	            type:'line',
	            data:${baiduCount}
	        }
	    ]
	};
	echarts.init(document.getElementById("baiduTongji")).setOption(option);
	
	option = {
	    title : {
	        text: "IP段访问次数",
	        subtext: '',
	        x:'center'
	    },
	    legend: {
	        orient: 'vertical',
	        left: 'left',
	        data: ${singleData}
	    },
	    series : [
	        {
	            name: '数据统计',
	            type: 'pie',
	            radius : '55%',
	            center: ['50%', '60%'],
	            data:${data},
	            itemStyle: {
	                emphasis: {
	                    shadowBlur: 10,
	                    shadowOffsetX: 0,
	                    shadowColor: 'rgba(0, 0, 0, 0.5)'
	                }
	            }
	        }
	    ]
	};
	echarts.init(document.getElementById('spiderPan')).setOption(option);
  </script>
</html>