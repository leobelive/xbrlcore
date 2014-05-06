<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglibs.jsp"%>
  <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" />
  <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
  <script src="http://code.jquery.com/ui/1.10.2/jquery-ui.js"></script>
  <script src="${ctx}/js/jquery/jquery-ui-timepicker-addon.js"></script>
 <style>
	textarea#content { margin-left:12px; width:90%; }
	fieldset { padding:10px; border:10px; margin-top:25px;pargin-left:25px;height:50px; }
	textarea,label,select{margin-top:10px;margin-bottom:10px;}
 </style>
<script>
$(function() {
 
    $( "#add" ).dialog({
      autoOpen: false,
      height: 400,
      width: 650,
      modal: true,
      resizable:false,
      buttons: {
        "创建提醒": function() {
        			var time;
					if ( $("select#alarmType").val() == "3")
						time = $("#datepicker").val();
					else
						time = $("#timepicker").val();
					if (( $("#datepicker").val() == "") && ($("#timepicker").val() == "")){
						alert("请选择提醒时间");
        			}
    				else if ($("textarea#content").val().replace( /\r?\n/g, "\r\n" )==""){
						alert("请填写提醒内容");
        			}
					else {
        			$.post("${ctx}/save",
        						{content: $("textarea#content").val(),
        						   type: $("#alarmType").val(),
        						   time: time
        						  },
        						function(data){							  													
        							if (data == "ok") {
        								$("#datepicker").val("");
        								$("#timepicker").val("");
        								$("textarea#content").val("");
        								 $("#ok").fadeToggle(2000);
        								 $("#ok").fadeToggle(2000,function(){$( "#add" ).dialog( "close" );});      								  
        							}						   
        						});	
					}

        },
        "取消": function() {
          $( this ).dialog( "close" );
        }
     }   
    });
    
    //提醒类型选择datapicker或timepicker
    $("select#alarmType").change(function(){
    	
    	if ( $("select#alarmType").val() == "3"){
    		$("#datepicker").show();
    		$("#timepicker").hide();
    		$("#datepicker").datetimepicker({
    			showSecond: true,
    			timeFormat: 'HH:mm:ss',
    			stepHour: 2,
    			stepMinute: 10,
    			stepSecond: 10
    		});
    	}else {
    		$("#datepicker").hide();
    		$("#timepicker").show();
    	} 
    });
 
    $( "#creat-alarm" )
      .button()
      .click(function() {
        $( "#add" ).dialog( "open" );
      });
    
    $("textarea#content").addClass("text ui-widget-content ui-corner-all");
    $("select#alarmType").addClass("select ui-widget-content ui-corner-all");
    $( "#timepicker" ).timepicker({
    	showSecond:true,
    	hourGrid: 4,
    	minuteGrid: 10,
    	timeFormat: 'hh:mm:ss'
    });
	$("#datepicker").hide();
	$("#ok").hide();
	var curpage = ${curpage};
	var p="";
	for (var i=(curpage-1); i<(curpage-1)+6;i++){
		p =p+"<a href='${ctx}/list/"+i+"' style='margin-right:10px'>"+i+"</a>";
	}
	$("#pages").html(p);
  });

</script>

<div id="content">

<button id="creat-alarm">创建提醒</button>
<div id="add" title="创建提醒" class="ui-widget ui-widget-content ui-corner-all">

<c:url var="saveUrl" value="/main/edit/${notice.aid}" />
<form:form modelAttribute="notice" method="POST" action="${saveUrl}" >
<fieldset>
	<form:label path="content" >内容</form:label>
	<form:textarea  path="content" cols="20" rows="3" /><br>
	<form:label path="alarmType">提醒类型</form:label>
	<form:select path="alarmType"  items="${types}" itemLabel="type" itemValue="id"/>
	<input type="text" id="timepicker" readonly>
	<input type="text" id="datepicker" readonly>
</fieldset>
</form:form>

<div id="ok" style="align:center;margin-left:40%;margin-top:20%">
<b>提醒创建成功</b>
</div>
<div id="messageBox"  style="display:none">error</div>
</div>


<c:forEach items="${notices}" var="n">
		<div class="post">
		<p class="meta"><span class="date">January 26, 2012  ${id}</span></p>
		<div class="entry">
						<p>${n.content}</p>
						<p class="links"><a href="#" class="more">编辑</a><a href="#" title="b0x" class="comments">广播</a></p>
					</div>
	
		</div>
		
</c:forEach>
  <p id="pages"></p>
				<div style="clear: both;">&nbsp;</div>		
</div>