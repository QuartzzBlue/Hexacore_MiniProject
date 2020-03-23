<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>

<style>
/* Global CSS Start ****************/
*{
	margin:0;padding:0;
}
a{
	text-decoration: none;
	color:black;
}
ul, ol{
	list-style: none;
}
/* Global CSS End ******************/



/* Header CSS Start ******************/
header{
	margin:0 auto;
	width:600px;
	height:150px;
	background:lightgrey;
	position: relative;
}

header > hgroup{
	position: absolute;
	top:30px;
	left:130px;
	text-align: center;
}
/* Header CSS End   ******************/


/* Nav CSS Start ******************/
nav{
	margin:0 auto;
	width:600px;
	height:30px;
	background:pink;
}
/* Nav CSS End   ******************/

/* Section CSS Start ******************/
section{
	margin:0 auto;
	width:600px;
	height:350px;
	background:white;
}

/* Section CSS End   ******************/

/* Footer CSS Start ******************/

/* Footer CSS End   ******************/


</style>
<script>

</script>
</head>
<body>
<header>

	<hgroup>
		<h1>Network Web Server</h1>
	</hgroup>
</header>
<section>
	<c:choose>
		<c:when test="${center == null }">
			<jsp:include page="center.jsp"/>
		</c:when>
		<c:otherwise>
			<jsp:include page="${center }.jsp"/>
		</c:otherwise>
	</c:choose>
</section>
<footer></footer>
</body>
</html>



    
