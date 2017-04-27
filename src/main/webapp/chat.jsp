<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/stylesheets/chat.css?v=2" rel="stylesheet" type="text/css">
<script src="http://reali.kr/js/jquery.min.js" type="text/javascript"></script>

<script type="text/javascript">
function press(f){
	 if(f.keyCode == 13){ //javascript에서는 13이 enter키를 의미함 
		 inputMessage.submit(); //formname에 사용자가 지정한 form의 name입력  
	 	}
	 } 

</script>
<title>Chat.jsp</title>
</head>
<div id="chat">
	<div id="chat-userlist">유저목록</div>
	<div id="chat-image">
		<div id="chat-image-area">
			<canvas id="chat-image-area-canvas" width="250" height="250"></canvas>
			<ul id="chat-image-area-colors">
				<li style="background-color: black;"></li>
				<li style="background-color: red;"></li>
				<li style="background-color: green;"></li>
				<li style="background-color: orange;"></li>
				<li style="background-color: brown;"></li>
				<li style="background-color: #d2232a;"></li>
				<li style="background-color: #fcb017;"></li>
				<li style="background-color: #fff460;"></li>
				<li style="background-color: #9ecc3b;"></li>
				<li style="background-color: #fcb017;"></li>
				<li style="background-color: #fff460;"></li>
				<li style="background-color: #F43059;"></li>
				<li style="background-color: #82B82C;"></li>
				<li style="background-color: #0099FF;"></li>
				<li style="background-color: #ff00ff;"></li>
			</ul>
			<div id="chat-image-area-tool">
				<div id="chat-image-area-tool-brush">
					<label for="brush">선의 두께:</label> 
					<input name="brush"	id="brush_size" type="range" value="5" min="0" max="100" />
				</div>
				<div id="chat-image-area-tool-control">
					<button id="undo" href="#" disabled="disabled">Undo</button>
					<button id="clear" href="#">Reset</button>
					<button id="export" href="#">Export as Image</button>
				</div>
			</div>
		<div id="chat-image-list">
			<div id="chat-image-list-display">이미지 목록</div>
			<div id="chat-image-list-control">이미지 추가 삭제 버튼</div>
		</div>
	</div>
	<div id="chat-dialogue">
		<div id="chat-dialogue-list">
		 <textarea id="messageWindow" rows="15" cols="38" readonly="true"></textarea>
        <br/>
		</div>
		<div id="chat-dialogue-input">
         <input id="inputMessage" type="text" onKeyPress="javascript:if(event.keyCode == 13) { send() }"/>
         <input type="submit" value="send" onclick="send()" />
        </div>
	</div>
</div>
</html>
 <script type="text/javascript">
 var textarea = document.getElementById("messageWindow");
 var webSocket = new WebSocket('ws://localhost:7070/broadcasting');
 var inputMessage = document.getElementById('inputMessage');
    webSocket.onerror = function(event) {
      onError(event)
    };
    webSocket.onopen = function(event) {
      onOpen(event)
    };
    webSocket.onmessage = function(event) {
        onMessage(event)
      };
    function onMessage(event) {
        textarea.value += "상대 : " + event.data + "\n";
    }
    function onOpen(event) {
        textarea.value += "연결 성공\n";
    }
    function onError(event) {
      alert(event.data);
    }
    function send() {
        textarea.value += "나 : " + inputMessage.value + "\n";
        webSocket.send(inputMessage.value);
        inputMessage.value = "";
    }
  </script>