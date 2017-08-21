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
    console.log(json);
}

init = function () {
    ws = new WebSocket("ws://localhost:8090/cache/websocket", "dima_v1");
    ws.onopen = function (event) {
        console.log("Opened", event.data)
    }
    ws.onmessage = function (event) {
        console.log("Message", event.data)
    }
    ws.onclose = function (event) {
       console.log("Close", event.data)
    }

    document.getElementById("btnNewValue")
            .addEventListener("click", function (event) {
        var $inputNewKey = document.getElementById("inputNewKey");
        var $inputNewValue = document.getElementById("inputNewValue");
        sendRequest("add", {
            "key": $inputNewKey.value,
            "value": $inputNewValue.value
        });
    });

    document.getElementById("btnReadValue")
             .addEventListener("click", function (event) {
        var $inputReadKey = document.getElementById("inputReadKey");
        sendRequest("get", {
            "key": $inputReadKey.value,
        });
    });

};
