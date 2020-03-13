<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="spring" 
uri="http://www.springframework.org/tags" %>

<script>
	function display(data){
		$('h3').text(data);
		console.log("displayData()");
	};
	function getData(){
		$.ajax({
			url:'hello.mc',
			success:function(data){
				display(data);
			},
			error : function(){}});
		console.log("getData()");
	};
	$(document).ready(function(){
		getData();
		setInterval(getData,1000);
	});
	function onclicked(){
		var url = 'webapp.mc?'
		var ip = document.getElementById('ip').value;
		var txt = document.getElementById('txt').value;
		if(ip!=null && ip!="") url+=('ip='+ip);
		if(txt!=null && txt!="") url+=('txt='+txt);
		/* var url = 'webapp.mc?ip='+ip+'&txt='+txt; */
		$.ajax({
			url:url,
			success:function(data){
				console.log("onclicked success"+data);
			},
			error : function(){
				console.log("onclicked error");
			}});
	}
</script>

<h1>Data from Pad</h1>
<h3>: Data...</h3> 
<h4>httpconnction.mc :IoT -> Pad -> Spring, 값 받으면 App으로 notification</h4>
<h1>Control Center</h1>
<form action="webapp.mc" method="get">
IP<input type="text" name="ip" id="ip"><br>
TXT<input type="text" name="txt" id="txt"><br>
<!-- <input type="submit" value="send"> -->

<h4>webapp.mc : send button 누르면 TCP/IP server -> Pad -> IoT, Pad로 notification 보냄</h4>
</form>
<button onclick = "onclicked()" >전송</button>