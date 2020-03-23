<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
#datazone{
background-color :#CEECF5;
}
#controlzone{
background-color:#F5ECCE;
}

</style>
<script>

	var flag = true;

	function display(data) {
		
		data = data.split(",");
		console.log(data);
		$('#FLId').text(data[0]);
		console.log(data[0]);
		if(data[1]=="Device1"){
			$('#temp').text(data[2]);
		}
		else if(data[1]=="Device2"){
			$('#vel').text(data[2]);
		}
		else if(data[1]=="Device3"){
			$('#rpm').text(data[2]);
		}
		else if(data[1]=="Device4"){
			$('#gas').text(data[2]);
		}
	};
	function getData() {
		$.ajax({
			url : 'ReturnData.mc',
			success : function(data) {
						display(data);
			},
			error : function() {
			}
		});
	};
	function DataRecv() {
			getData();
			setInterval(getData, 1000);
	};

	function SendServer() {
		var url = 'webapp.mc?'
		var FLid = document.getElementById('FLid').value;
		var ecuid = document.getElementById('ecuid').value;
		var data = document.getElementById('data').value;
		if(FLid==null||FLid==""){
			alert("제어할 ForkLift의 id를 입력하세요!");
		}
		if (data == "0" || data == "1") {
			url += ('FLid='+FLid+'&ecuid=' + ecuid+'&data='+data);
			$.ajax({
				url : url,
				success : function(data) {
					console.log("Sent to Server" + data);
				},
				error : function() {
					console.log("Send to Server Error");
				}
			});
		} else {
			alert("유효한 제어자를 입력하세요!");
		}

	}
</script>
<div id = "datazone">
<h1>Data from Pad</h1>
<h3>Forklift ID : </h3>
<span id = "FLId">
</span>

<h3>Temperature : </h3>
<span id = "temp">
</span>

<h3>Velocity : </h3>
<span id = "vel">
</span>

<h3>RPM : </h3>
<span id = "rpm">
</span>

<h3>GAS : </h3>
<span id = "gas">
</span>
<button onclick="DataRecv()">Receive from Pad</button>
<h4>httpconnction.mc :IoT -> Pad -> Spring, 값 받으면 App으로
	notification</h4>
</div>
<div id = "controlzone">
<h1>Control Center</h1>
<form action="webapp.mc" method="get">
	Forklift ID : <input type="text" name="fl" id="FLid"><br>
	ECU : <input type="text" name="ip" id="ecuid"><br> 
	ON/OFF : <input type="text" name="txt" id="data"><br>
	

	<h4>webapp.mc : send button 누르면 TCP/IP server -> Pad -> IoT, Pad로
		notification 보냄</h4>
</form>
<button onclick="SendServer()">Send to Server</button>
</div>