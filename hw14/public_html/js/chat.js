var ws;

function guid() {
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  }
  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
    s4() + '-' + s4() + s4() + s4();
}

function sendRequest(method, params) {
    var request = {
        "id": guid(),
        "method": method,
        "params": params
    };
    var json = JSON.stringify(request);
    ws.send(json);
}

init = function () {
    ws = new WebSocket("ws://localhost:8091/wschat");
    ws.onopen = function (event) {

    }
    ws.onmessage = function (event) {
        var $textarea = document.getElementById("messages");
        $textarea.value = $textarea.value + event.data + "\n";
    }
    ws.onclose = function (event) {

    }
};