<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="spring" 
uri="http://www.springframework.org/tags" %>
<style>
#uu{
	width:300px;
	height:200px;
	border:2px solid red;
}
</style>
<script>
function display(data){
	var result='';
	$(data).each(function(idx,item){
		result += '<h3>';
		result += item.id+''+item.pwd+''+item.name;
		result += '<h3>';
		
	});
	$('#uu').html(result);
};
function getData(){
	$.ajax({
		url:'uu.mc',
		success:function(data){
			display(data);
		}
	});
};
$(document).ready(function(){
	getData();
});
</script>
<h1>Main Center</h1>
<h2><spring:message code="welcome.txt" 
arguments="hi,mulcam"/></h2>
<div id="uu"></div>

