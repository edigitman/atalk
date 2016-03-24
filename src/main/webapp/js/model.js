// jquery plugin for enterKey
$.fn.enterKey = function (fnc) {
    return this.each(function () {
        $(this).keyup(function (ev) {
            var keycode = (ev.keyCode ? ev.keyCode : ev.which);
            if (keycode == '13') {
                fnc.call(this, ev);
            }
        })
    })
};

// page View Model
function TalkViewModel() {
    var self = this;
    inputText = ko.observable('');
    titleInput = ko.observable('Name:');
    isConnected = ko.observable(false);
    usersList = ko.observableArray();
    username = ko.observable('');

    var webSocket;
    var output = $("#output");

    this.openConnection = function () {

        // open the connection if one does not exist
        if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
            return;
        }

        if (inputText() == '') {
            alert('Name mandatory !');
            return;
        }
        username(inputText());
        // Create a websocket
        webSocket = new WebSocket("ws://45.32.184.20/talk/" + name);
        //webSocket = new WebSocket("ws://localhost:8080/talk/" + username());

        webSocket.onopen = function (event) {
            $("#userStatus").text("Connected as [" + username() + "]!");
            isConnected(true);
            titleInput('Text:');
            inputText('');
            $("#input").focus();
        };

        webSocket.onmessage = function (event) {
            var obj = JSON.parse(event.data);
            if (obj.type == 'users') {
                self.loadUsers(obj.users);
            } else {
                if (obj.type == "po") {
                    usersList.removeAll();
                    self.loadUsers(obj.users);
                } else {
                    self.updateOutput(obj);
                }
            }
        };

        webSocket.onclose = function (event) {
            $("#userStatus").text("Connection Closed");
            isConnected(false);
        };
    };

    this.loadUsers = function(arr){
        $.each( arr, function( index, value ){
            usersList.push(value);
        });
    };

    this.updateOutput = function (obj) {
        output.append("<br/>" + obj.date + " [" + obj.sender + "]: " + obj.text);
        output.prop({scrollTop: output.prop("scrollHeight")});
    };

    this.send = function () {
        if (webSocket != undefined && webSocket.readyState == WebSocket.OPEN && inputText() !== '') {
            var obj = {type: 'msg', sender: username(), text: inputText()};
            webSocket.send(JSON.stringify(obj));
            inputText('');
            $("#input").focus();
        }
    };

    $("#input").enterKey(function () {
        inputText.valueHasMutated();
        self.send();
    });

    window.setInterval(function () {
        if (webSocket != undefined && webSocket.readyState == WebSocket.OPEN) {
            var obj = {type: 'ping', sender: username()};
            webSocket.send(JSON.stringify(obj));
        }
    }, 2000);
}

ko.applyBindings(new TalkViewModel());
