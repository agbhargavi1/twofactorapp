var stompClient = null;

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
        	if(JSON.parse(greeting.body).content === sessionStorage.jti) {
        		  $.ajax({
		      type: "POST",
		      url: "/logout",
		          beforeSend: function (xhr) {
			    	    xhr.setRequestHeader ("Authorization", sessionStorage.access_token);
			    	},
		    }).done(function (data) {
		    sessionStorage.clear();
		     	document.location.href="/";
		    }).fail(function (jqXHR, textStatus) {
		    sessionStorage.clear();
		    		document.location.href="/";
			});
        	}
        });
    });
}
connect();
setTimeout(function(){ 

if(sessionStorage.previousToken) {
stompClient.send("/app/hello", {}, JSON.stringify({'name': sessionStorage.previousToken}));} }, 3000);
