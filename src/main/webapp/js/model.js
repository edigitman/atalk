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

    var name = '';

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
        name = inputText();
        // Create a websocket
        webSocket = new WebSocket("ws://45.32.184.20/talk/" + name);
        //webSocket = new WebSocket("ws://localhost:8080/talk/" + name);

        webSocket.onopen = function (event) {
            $("#userStatus").text("Connected as [" + name + "]!");
            isConnected(true);
            titleInput('Text:');
            inputText('');
            $("#input").focus();
        };

        webSocket.onmessage = function (event) {
            self.updateOutput(event.data);
        };

        webSocket.onclose = function (event) {
            self.updateOutput("Connection Closed");
            isConnected(false);
        };
    };

    this.updateOutput = function (text) {
        output.append("<br/>" + text);
        output.prop({scrollTop: output.prop("scrollHeight")});
    };

    this.send = function () {
        if (webSocket != undefined && webSocket.readyState == WebSocket.OPEN && inputText() !== '') {
            webSocket.send(inputText());
            inputText('');
            $("#input").focus();
        }
    };

    $("#input").enterKey(function () {
        inputText.valueHasMutated();
        self.send();
    });

    window.setInterval(function() {
        $.get( "/users", function( data, status ) {
            $('.participants').text(data);
        });
    }, 2000);
}

ko.applyBindings(new TalkViewModel());