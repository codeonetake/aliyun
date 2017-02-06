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
  		<div id="memTongji" style="width:100%;height:350px"></div>
  		<hr/>
  		<div class="row">
  		<div class="col-md-6" id="mem" style="width:50%;height:350px"></div>
  		<div class="col-md-6" id="diskTongji" style="width:50%;height:350px"></div>
  		</div>
  	</div>
  </body>
  <script type="text/javascript">
  	var option = {
	    title: {
	        text: '内存使用量',
	        subtext: '更新时间：${lastTime}'
	    },
	    tooltip: {
	        trigger: 'axis'
	    },
	    legend: {
	        data:['used','buff','cach','free']
	    },
	    grid: {
	        left: '1%',
	        right: '0%',
	        bottom: '10%',
	        containLabel: true
	    },
	    toolbox: {
	    		show : true,
	        orient: 'horizontal',      // 布局方式，默认为水平布局，可选为：
	        x: 'right',                // 水平安放位置，默认为全图右对齐，可选为：
	        y: 'top',                  // 垂直安放位置，默认为全图顶端，可选为：
	        color : ['#1e90ff','#22bb22','#4b0082','#d2691e'],
	        backgroundColor: 'rgba(0,0,0,0)', // 工具箱背景颜色
	        borderColor: '#ccc',       // 工具箱边框颜色
	        borderWidth: 0,            // 工具箱边框线宽，单位px，默认为0（无边框）
	        padding: 5,                // 工具箱内边距，单位px，默认各方向内边距为5，
	        showTitle: true,
	        feature : {
	            dataZoom : {
	                show : true,
	                title : {
	                    dataZoom : '区域缩放',
	                    dataZoomReset : '区域缩放-后退'
	                }
	            },
	            magicType: {
	                show : true,
	                title : {
	                    line : '动态类型切换-折线图',
	                    bar : '动态类型切换-柱形图',
	                    stack : '动态类型切换-堆积',
	                },
	                type : ['line', 'bar', 'stack']
	            },
	            restore : {
	                show : true,
	                title : '还原',
	                color : 'black'
	            },
	            saveAsImage : {
	                show : true,
	                title : '保存为图片',
	                type : 'jpeg',
	                lang : ['点击本地保存'] 
	            },
	        }
	    },
	    dataZoom : {
	        show : true,
	        realtime : true,
	        y:320,
	        start : 0,
	        end : 999
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
	            name:'used',
	            type:'line',
	            data:${usedCount},
		        itemStyle : {  
	                normal : {
	                	color:'#d43f3a',
	                    lineStyle:{  
	                        color:'#d43f3a'  
	                    }  
	                }  
	            }
	        },
	        {
	            name:'buff',
	            type:'line',
	            data:${buffCount},
	            itemStyle : {  
	                normal : {
	                	color:'#2e6da4',
	                    lineStyle:{  
	                        color:'#2e6da4'  
	                    }  
	                }  
	            }
	        },
	        {
	            name:'cach',
	            type:'line',
	            data:${cachCount},
	            itemStyle : {  
	                normal : {
	                	color:'#46b8da',
	                    lineStyle:{  
	                        color:'#46b8da'  
	                    }  
	                }  
	            }
	        },
	        {
	            name:'free',
	            type:'line',
	            data:${freeCount},
	            itemStyle : {  
                    normal : {
                    	color:'#4cae4c',
                        lineStyle:{  
                            color:'#4cae4c'  
                        }  
                    }  
                } 
	        }
	    ]
	};
	echarts.init(document.getElementById("memTongji")).setOption(option);
	
	option = {
	    title : {
	        text: "磁盘使用量",
	        subtext: '可用：${diskTongji.free}M，已用：${diskTongji.use}M',
	        x:'center'
	    },
	    color:['#265a88','#afafaf',],
	    legend: {
	        orient: 'vertical',
	        left: 'left',
	        data: ['可用空间','已用空间']
	    },
	    series : [
	        {
	            name: '数据统计',
	            type: 'pie',
	            radius : '55%',
	            center: ['50%', '60%'],
	            data:[{
	            		value:${diskTongji.free},
	            		name:"可用空间"
	            	},
	            {
	            		value:${diskTongji.use},
	            		name:"已用空间"
	            	}],
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
	echarts.init(document.getElementById('diskTongji')).setOption(option);
	
	option = {
	    title : {
	        text: "内存使用率",
	        subtext: '更新时间：${lastTime}',
	        x:'center'
	    },
	    color:['#d43f3a','#2e6da4','#46b8da','#4cae4c'],
	    legend: {
	        orient: 'vertical',
	        left: 'left',
	        data: ['used','buff','cach','free']
	    },
	    series : [
	        {
	            name: '数据统计',
	            type: 'pie',
	            radius : '55%',
	            center: ['50%', '60%'],
	            data:[{
	            		value:${memDetail.used},
	            		name:"used"
	            	},
	            {
	            		value:${memDetail.buff},
	            		name:"buff"
	            	},{
	            		value:${memDetail.cach},
	            		name:"cach"
	            	},{
	            		value:${memDetail.free},
	            		name:"free"
	            	}],
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
	echarts.init(document.getElementById('mem')).setOption(option);
  </script>
</html>