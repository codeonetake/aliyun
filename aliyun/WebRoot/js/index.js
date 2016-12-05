var addErrorSecondInterval = null;
$(document).scroll(function(){
	$("#msgBox").css("top",$(document).scrollTop()+20);
});
function hideMsgBox(){
	$("#msgBox").slideUp(300,function(){
		$("#msgBox").removeClass("alert-warning");
		$("#msgBox").removeClass("alert-danger");
		$("#msgBox").removeClass("alert-success");
		$("#msgBox").addClass("alert-success");
	});
}
//showMsgBox("hello","alert-success");
function showMsgBox(data,clazz){
	$("#msgBox").removeClass("alert-success");
	$("#msgBox").addClass(clazz);
	$("#msgBox").css("top",$(document).scrollTop()+20);
	$("#msgBox").css("margin-left",($(document).width()-1.12*$("#msgBox").css("width").replace("px","")));
	$("#msgBox").html(data);
	$("#msgBox").slideDown(200);
	hideMsgBoxDH();
}
function hideMsgBoxDH(){
	setTimeout("hideMsgBox()",3000);
}
//showMsgBox("hello");
String.prototype.endWith=function(endStr){
  	var d=this.length-endStr.length;
  	return (d>=0&&this.lastIndexOf(endStr)==d)
}
var current = null;
function dismiss(){
	if(null != current){
		current.popover('hide');
	}
}
$("#searchBtn").click(function(){
	if($("#host").val().indexOf(".mapbar.")==-1){
		$("#host").val($("#host").val()+".mapbar.com");
	}
	if($("#searchBtn").html() == "查询"){
		$.ajax({
			url:"/search",
			type:"POST",
			data:"host="+$("#host").val()+"&nextPath="+$("#nextPath").val(),
			beforeSend:function(){
				$("#historyResult").html("");
				$("#result").html("查询中...");
				$("#searchBtn").html("&nbsp;<span class=\"glyphicon glyphicon-time\"></span>&nbsp;");
			},
			success:function(data){
				$("#tableDemo table").html("<tr><td>path名称</td><td>base路径</td></tr>");
				var isAdd = false;
				var isAutoSave = true;
				var isError = false;
				var content = "";
				$("#searchBtn").html("查询");
				var count = 0;
				$.each(data,function(i,n){
					if(n.type == "info" || n.type == "file"){
  						count ++;
  						content += count+". ";
						content += n.info;
						content += "<br/>";
					}
					if(n.type == "error"){
						count ++;
						content += "<font color='red'>"+ count + ". " + n.info + "</font>";
						content += "<br/>";
						isAutoSave = false;
						isError = true;
					}
					if(n.type == "content"){
						content += "<textarea class='form-control' style='width:100%;height:200px'>" + n.info + "</textarea>";
						content += "<br/>";
					}
					if(n.type == "table"){
						isAdd = true;
						var t = n.info.split("|");
						$("#tableDemo table").append("<tr><td>"+t[0]+"</td><td>"+t[1]+"</td></tr>");
					}
				});
				if(isAdd){
  					content += "路径整理："+$("#tableDemo").html();
				}
				//添加自动纠错
				if(isError){
					$.ajax({
						url:"/checkServer",
						type:"POST",
						data:"host="+$("#host").val()+"&nextPath="+$("#nextPath").val(),
						success:function(data){
							if("exist" == data){
								content+="<code><a href='javascript:;' onclick='showErrorHistory()'>查看纠错信息</a></code>";
								content+="&nbsp;&nbsp;&nbsp;&nbsp;";
							}
							content+="<code><a href='javascript:;' onclick='recordError()'>纠错</a></code>";
							$("#result").slideUp(500,function(){
								$("#result").html(content+"<br/>");
								$("#result").slideDown(1000);
							});
						}
					});
				}else{
					$("#result").slideUp(500,function(){
						$("#result").html(content+"<br/>");
						$("#result").slideDown(1000,function(){
							if(content.indexOf("</table>") != -1){
								autoSave("自动保存服务器信息成功","自动保存服务器信息失败");
							}
						});
					});
				}
				$(".copy").attr("data-toggle","popover");
				$(".copy").attr("data-placement","top");
				$(".copy").attr("data-content","复制成功");
				$(".copy").attr("data-trigger","focus");
  				$(".copy").dblclick(function(){
  					current = $(this);
  	  	  			$.ajax({
  	  	  				url:"/copy",
  	  	  				type:"POST",
  	  	  				data:"content="+$(this).html(),
  	  	  				beforeSend:function(){
	  	  	  				current.popover('show');
  	  	  					setTimeout("dismiss()",1000);
  	  	  				}
  	  	  			});
  	  	  		});
			}
		});
	}
});
function recordHistory(){
	$("#recordHistoryModal").modal("show");
	if(""==$("#nextPath").val()){
		$("#serverUrl").html("<code>"+$("#host").val()+"</code>");
	}else{
		$("#serverUrl").html("<code>"+$("#host").val()+"/"+$("#nextPath").val()+"</code>");
	}
	$("#addResult").html("");
	$("#serverXmlPath").val("");
	$("#contextText").val("");
}
$("#saveHistory").click(function(){
	$.ajax({
		url:"/saveHistory",
		data:"host="+$("#host").val()+"&nextPath="+$("#nextPath").val()+"&serverXmlPath="+$("#serverXmlPath").val()+"&contextText="+$("#contextText").val(),
		type:"POST",
		success:function(data){
			var content = "";
			var color = "red";
			if("host_empty" == data){
				content="域名不能为空";
			}
			if("nextPath_empty"==data){
				content="二级目录不能为空";
			}
			if("success"==data){
				color = "black";
				content="保存成功";
			}
			if("fail"==data){
				content="保存失败";
			}
			if("serverXmlPath_empty" == data){
				content="server.xml完整路径不能为空";
			}
			if("contextText_empty" == data){
				content="context的完整信息不能为空";
			}
			$("#addResult").html("<font color='"+color+"'>"+content+"</font>");
		}
	});
});
function showHistory(){
	$.ajax({
		url:"/showHistory",
		data:"host="+$("#host").val()+"&nextPath="+$("#nextPath").val(),
		type:"POST",
		beforeSend:function(){
			$("#historyResult").html("查询中...");
		},
		success:function(data){
			var isAutoSave = true;
			if(""==data){
				$("#historyResult").html("<font color='red'><b>获取信息失败，记录可能不存在或者请求的数据有问题</b></font>");
			}else{
				$("#tableDemo table").html("<tr><td>path名称</td><td>base路径</td></tr>");
				var isAdd = false;
				var content = "";
				$("#searchBtn").html("查询");
				var count = 0;
				$.each(data,function(i,n){
					if(n.type == "info" || n.type == "file"){
  						count ++;
  						content += count+". ";
						content += n.info;
						content += "<br/>";
					}
					if(n.type == "error"){
						count ++;
						content += "<font color='red'>"+ count + ". " + n.info + "</font>";
						content += "<br/>";
						isAutoSave = false;
					}
					if(n.type == "content"){
						content += "<textarea class='form-control' style='width:100%;height:200px'>" + n.info + "</textarea>";
						content += "<br/>";
					}
					if(n.type == "table"){
						isAdd = true;
						var t = n.info.split("|");
						$("#tableDemo table").append("<tr><td>"+t[0]+"</td><td>"+t[1]+"</td></tr>");
					}
				});
				if(isAdd){
  					content += "路径整理："+$("#tableDemo").html();
				}
				$("#historyResult").html(content+"<br/>");
				//没有报错的情况下，自动保存信息
				if(isAutoSave && content.indexOf("</table>") != -1){
  					autoSave("自动保存服务器信息成功","自动保存服务器信息失败");
				}
			}
			//鼠标滚动
			$("html,body").animate({scrollTop:$(document).height()},1500);
		}
	});
}

function autoSave(successInfo,errorInfo){
	var content = $("#result").html()+$("#historyResult").html();
	$.ajax({
		url:"/autoSave",
		type:"POST",
		data:"content="+encodeURIComponent(content)+"&host="+$("#host").val()+"&nextPath="+$("#nextPath").val(),
		success:function(data){
			if("success"==data){
				showMsgBox(successInfo,"alert-success");
			}else{
				showMsgBox(errorInfo,"alert-danger");
			}
		}
	});
}
$("#host").focus();
$("#host").keydown(function(e){
	if(e.keyCode==13){
		$("#nextPath").focus();
	}
});
$("#nextPath").keydown(function(e){
	if(e.keyCode==13){
		$("#searchBtn").click();
		$("#nextPath").blur();
	}
});
$(document).keydown(function(e){
	if(e.keyCode == 36){
		$("#host").focus();
	}
});
//纠错功能
function recordError(){
	$("#recordErrorModal").modal("show");
	if(""==$("#nextPath").val()){
		$("#errorServerUrl").html("<code>"+$("#host").val()+"</code>");
	}else{
		$("#errorServerUrl").html("<code>"+$("#host").val()+"/"+$("#nextPath").val()+"</code>");
	}
	$("#nginxIp").val("");
	$("#nginxPath").val("");
	$("#vhostsText").val("");
	$("#upstreamText").val("");
	$("#tomcatIp").val("");
	$("#errorServerXmlPath").val("");
	$("#contextInfo").val("");
	$("#errorAddResult").html("");
}
$("#saveErrorServer").click(function(){
	//使用自动保存的功能
	var d = "1. 前端服务器的ip为：<code>"+$("#nginxIp").val()+"</code><br/>";
	d+="2. 前端服务(nginx或apache)的路径为：<code>"+$("#nginxPath").val()+"</code><br/>";
	d+="3. 前端文件（vhosts）的配置信息如下：<br/>";
	d+= "<textarea class='form-control' style='width:100%;height:300px'>"+$("#vhostsText").val()+"</textarea><br/>"
	d+="4. 前端文件（upstream）的配置信息如下：<br/>";
	d+= "<textarea class='form-control' style='width:100%;height:300px'>"+$("#upstreamText").val()+"</textarea><br/>"
	d+="5. tomcat服务器ip为：<code>"+$("#tomcatIp").val()+"</code><br/>";
	d+="6. tomcat服务器server.xml配置文件路径为：<code>"+$("#errorServerXmlPath").val()+"</code><br/>";
	d+="7. tomcat服务器server.xml中Context配置信息如下：<br/>";
	d+= "<textarea class='form-control' style='width:100%;height:300px'>"+$("#contextInfo").val()+"</textarea>";
	$("#recordErrorModal").modal("hide");
	$("#result").slideUp(300,function(){
		$("#result").html(d);
		$("#result").slideDown(1500,function(){
			$("#result").prepend("<div id='saveTip' style='display:none' class='alert alert-warning' role='alert'>本次记录将于<span id='addErrorSecond'>20</span>秒后自动保存，您可以 <a href='javascript:;' onclick='cancelAutoSave()'>取消自动保存</a> 或者 <a href='javascript:;' onclick='manualSave()'>点击立刻保存</a></div>");
			$("#saveTip").slideDown(300,function(){
				addErrorSecondInterval = setInterval("downSecond()",1000);
			});
		});
	});
});
function cancelAutoSave(){
	if(null != addErrorSecondInterval){
		clearInterval(addErrorSecondInterval);
		addErrorSecondInterval = null;
		$("#saveTip").html("已经取消自动保存，请手动点击 <a href='javascript:;' onclick='manualSave()'>保存</a>");
	}
}
function manualSave(){
	if(null != addErrorSecondInterval){
		clearInterval(addErrorSecondInterval);
		addErrorSecondInterval = null;
	}
	$("#saveTip").slideUp(300,function(){
		autoSave("已经成功保存","保存失败");
	});
}
function downSecond(){
	var second = $("#addErrorSecond").html()*1;
	second --;
	if(second == 0){
		if(null != addErrorSecondInterval){
			clearInterval(addErrorSecondInterval);
			addErrorSecondInterval = null;
		}
		$("#saveTip").slideUp(300,function(){
			autoSave("自动保存服务器信息成功","自动保存服务器信息失败");
		});
	}else{
		$("#addErrorSecond").html(second);
	}
}
//查看维护记录功能
function showErrorHistory(){
	if(null != addErrorSecondInterval){
		clearInterval(addErrorSecondInterval);
		addErrorSecondInterval = null;
	}
	$.ajax({
		url:"/showErrorHistory",
		type:"POST",
		data:"host="+$("#host").val()+"&nextPath="+$("#nextPath").val(),
		success:function(data){
			if(data == "error"){
				$("#result").html("<font style='color:red'><b>查询记录出错</b></font>");
			}else if(data == "nodata"){
				$("#result").html("<font style='color:red'><b>暂无数据</b></font>");
			}else{
				//$("#result").html(data);
				$("#result").slideUp(300,function(){
					$("#result").html(data);
					$("#result").slideDown(1500);
				});
			}
		}
	});
}