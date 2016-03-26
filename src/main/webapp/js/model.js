// page View Model
function TalkViewModel() {
    var self = this;
    inputText = ko.observable('');
    isConnected = ko.observable(false);
    usersList = ko.observableArray();
    username = ko.observable('');

    var webSocket;
    var output = $("#output");

    var isWindowFocused;
    $(window).focus(function () {
        isWindowFocused = true;
        $(document).prop('title', 'aTalk');
    }).blur(function () {
        isWindowFocused = false;
    });

    this.disconnect = function(){
        webSocket.close();
        usersList.removeAll();
        Cookies.remove('aTalk', {expires: 30});
    };

    this.openConnection = function () {
        // open the connection if one does not exist
        if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
            return;
        }

        username(inputText());
        if (username() == '') {
            alert('Name mandatory !');
            return;
        }

        // Create a websocket
        webSocket = new WebSocket("ws://" + window.location.host + "/talk/" + username());
        webSocket.onopen = function (event) {
            self.changeStatus("Connected as [" + username() + "]!");
            isConnected(true);
            inputText('');
            $("#input").focus();
            Cookies.set('aTalk', username(), {expires: 30});
        };

        webSocket.onmessage = function (event) {
            var obj = JSON.parse(event.data);
            //first connect
            if (obj.type == 'connect') {
                self.loadUsers(obj.users);
                self.loadHistory(obj.todays);
            } else {
                // response to ping
                if (obj.type == "po") {
                    self.loadUsers(obj.users);
                } else {
                    //text message
                    self.showMessage(obj);
                }
            }
        };

        webSocket.onclose = function (event) {
            self.changeStatus("Connection Closed");
            isConnected(false);
        };
    };

    //empty and load list of users
    this.loadUsers = function (arr) {
        usersList.removeAll();
        $.each(arr, function (index, value) {
            usersList.push(value);
        });
    };

    //load list of messages from history
    this.loadHistory = function (hist) {
        $.each(hist, function (index, value) {
            self.updateOutput(value);
        });
    };

    // send text message to server is not empty, clean and focus input
    this.send = function () {
        if (webSocket != undefined && webSocket.readyState == WebSocket.OPEN && $.trim(inputText()) !== '') {
            webSocket.send(JSON.stringify({type: 'msg', sender: username(), text: inputText()}));

            inputText('');
            $("#input").focus();
        }
    };

    //display message and notify user
    this.showMessage = function (obj) {
        self.updateOutput(obj);
        if (!isWindowFocused) {
            self.outOfFocusNotification(obj);
        }
    };

    //add message to screen and scroll panel
    this.updateOutput = function (obj) {
        output.append("<div class='chatLine' title='" + obj.date + "'><b>" + obj.sender + "</b>: <span style='color: " + obj.color + "'>" + obj.text + "</span></div>");
        output.prop({scrollTop: output.prop("scrollHeight")});
    };

    // update page title if out of focus
    this.outOfFocusNotification = function (obj) {
        $.playSound('ding');
        if ($(document).prop('title').indexOf('>>>') == -1) {
            $(document).prop('title', '>>> ' + obj.sender + ': ' + obj.text);
        }
    };

    this.changeStatus = function (message) {
        $("#userStatus").html(message);
    };

    //enter pressed on text field
    $("#input").enterKey(function () {
        inputText.valueHasMutated();
        self.send();
    });

    // keep the socket open and update on users status
    window.setInterval(function () {
        if (webSocket != undefined && webSocket.readyState == WebSocket.OPEN) {
            var obj = {type: 'ping', sender: username()};
            webSocket.send(JSON.stringify(obj));
        }
    }, 2000);

    $(document).ready(function () {
        var name = Cookies.get('aTalk');
        if (name != undefined) {
            inputText(name);
            self.openConnection();
        }
    });
}

ko.applyBindings(new TalkViewModel());