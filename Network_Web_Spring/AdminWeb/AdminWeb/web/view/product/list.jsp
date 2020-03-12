<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
.center_page > img{
	width:80px;
	height:100px; 
}
</style>
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
</script>

<div class="center_page">
<h1>Data from Pad Page</h1>
<h3>Data...</h3> 
</div>