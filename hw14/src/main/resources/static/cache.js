var ws;

init = function () {
    ws = new WebSocket("ws://localhost:8090/cache/websocket");
    ws.onopen = function (event) {
        console.log("Opened", event.data)
    }
    ws.onmessage = function (event) {
        console.log("Message", event.data)
    }
    ws.onclose = function (event) {
       console.log("Close", event.data)
    }
};

function sendMessage() {
    var messageField = document.getElementById("message");
    var userNameField = document.getElementById("username");
    var message = userNameField.value + ":" + messageField.value;
    ws.send(message);
    messageField.value = '';
}