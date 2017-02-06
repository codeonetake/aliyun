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
  		<div class="row">
  			<div class="col-md-6" id="logTongji" style="width:50%;height:350px"></div>
  			<div class="col-md-6" id="logTongjiBox" style="width:50%;height:350px"></div>
  		</div>
  		<hr/>
  		<div>
		  <!-- Nav tabs -->
		  <ul class="nav nav-tabs" role="tablist">
		  	<c:forEach items="${logTypeCountTongji.details}" var="detail" varStatus="i">
		  		<c:if test="${detail.type != '200'}">
		  			<li role="presentation"><a class="taba" href="#type${detail.type}" aria-controls="type${detail.type}" role="tab" data-toggle="tab" type="${detail.type}">${detail.type}</a></li>
		  		</c:if>
		    </c:forEach>
		  </ul>
		
		  <!-- Tab panes -->
		  <div class="tab-content">
		  	<c:forEach items="${logTypeCountTongji.details}" var="detail" varStatus="i">
		  	<c:if test="${detail.type != '200'}">
		  		<div role="tabpanel" class="tab-pane" id="type${detail.type}"></div>
		  	</c:if>
		  	</c:forEach>
		  </div>
		
		</div>
		<hr/>
		<div>
		  <!-- Nav tabs -->
		  <ul class="nav nav-tabs" role="tablist">
		  	<li role="presentation"><a href="#errorLog" aria-controls="errorLog" role="tab" data-toggle="tab">Error (${fn:length(errors)})</a></li>
		  	<li role="presentation"><a href="#warnLog" aria-controls="warnLog" role="tab" data-toggle="tab">Warn (${fn:length(warns)})</a></li>
		  </ul>
		
		  <!-- Tab panes -->
		  <div class="tab-content">
		  	<div role="tabpanel" class="tab-pane" id="errorLog">
		  		<c:forEach items="${errors}" var="error">
					<div class="alert alert-danger" role="alert">${error}</div>
				</c:forEach>
		  	</div>
		  	<div role="tabpanel" class="tab-pane" id="warnLog">
		  		<c:forEach items="${warns}" var="warn">
					<div class="alert alert-warning" role="alert">${warn}</div>
				</c:forEach>
		  	</div>
		  </div>
		</div>
		<input id="keyword" type="text" class="form-control" style="display:inline;width:50%">
	  	<select id="logFile" class="form-control" style="display:inline;width:100px">
	  		<option value="access" selected="selected">access</option>
	  		<option value="error">error</option>
	  	</select>
	  	<button id="searchBtn" style="display:inline;" class="btn btn-primary">查找</button>
	  	<hr/>
	  	<div id="searchResult"></div>
  	</div>
  </body>
  <script type="text/javascript">
  	$("#searchBtn").click(function(){
  		var keyword = $("#keyword").val();
  		var logFile = $("#logFile").val();
  		if(keyword == ""){
  			return;
  		}
  		$.ajax({
  			url:"/tongji/getByKeyword",
  			type:"POST",
  			data:"keyword="+keyword+"&logFile="+logFile,
  			success:function(data){
  				var result = "";
  				var count = 0;
  				$.each(data,function(i,n){
  					result += "<div class=\"alert alert-default\" role=\"alert\">"+n+"</div>";
  					count ++;
  				});
  				$("#searchResult").html("<code>一共查询出"+count+"条数据</code><br/>"+result);
  			}
  		});
  	});
	var option = {
	    title : {
	        text: "HTTP响应码",
	        subtext: '更新时间：${logTypeCountTongji.time}',
	        x:'center'
	    },
	    legend: {
	        orient: 'vertical',
	        left: 'left',
	        data: ${legendData}
	    },
	    series : [
	        {
	            name: '数据统计',
	            type: 'pie',
	            radius : '55%',
	            center: ['50%', '60%'],
	            data:${datas},
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
	echarts.init(document.getElementById('logTongji')).setOption(option);
	
	$(".taba").click(function(){
		var type = $(this).attr("type");
		var val = $("#type"+type).html();
		if(val == ""){
			$.ajax({
				type:"POST",
				url:"/tongji/getInfoByType",
				data:"type="+type,
				success:function(data){
					var result = "<br/><code>更新时间为:"+data.time+"</code><hr/>";
					$.each(data.infos,function(i,n){
						result+="<div class=\"alert alert-info\" role=\"alert\">"+n+"</div>";
					});
					$("#type"+type).html(result);
					
				}
			});
		}
	});
	
	option = {
	    title : {
	        text: 'HTTP响应数量',
	        subtext: '更新时间：${logTypeCountTongji.time}'
	    },
	    tooltip : {
	        trigger: 'axis'
	    },
	    legend: {
	        data:['响应数量']
	    },
	    calculable : true,
	    xAxis : [
	        {
	            type : 'category',
	            data : ${legendData}
	        }
	    ],
	    yAxis : [
	        {
	            type : 'value'
	        }
	    ],
	    series : [
	        {
	            name:'响应数量',
	            type:'bar',
	            data:${values},
	        }
	    ]
	};
	echarts.init(document.getElementById('logTongjiBox')).setOption(option);               
  </script>
</html>