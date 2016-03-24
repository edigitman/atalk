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

(function ($) {
    $.extend({
        playSound: function () {
            return $(
                '<audio id="soundDiv" autoplay="autoplay" style="display:none;">'
                + '<source src="audio/' + arguments[0] + '.mp3" />'
                + '<embed src="audio/' + arguments[0] + '.mp3" hidden="true" autostart="true" loop="false" class="playSound" />'
                + '</audio>'
            ).appendTo('body');
        }
    });
})(jQuery);

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

    var window_focus;
    $(window).focus(function () {
            window_focus = true;
            $(document).prop('title', 'atalk');
        })
        .blur(function () {
            window_focus = false;
        });

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
        webSocket = new WebSocket("ws://" + window.location.host + "/talk/" + username());

        webSocket.onopen = function (event) {
            $("#userStatus").text("Connected as [" + username() + "]!");
            isConnected(true);
            titleInput('Text:');
            inputText('');
            $("#input").focus();
        };

        webSocket.onmessage = function (event) {
            var obj = JSON.parse(event.data);
            if (obj.type == 'connect') {
                self.loadUsers(obj.users);
                self.loadHistory(obj.todays);
            } else {
                if (obj.type == "po") {
                    usersList.removeAll();
                    self.loadUsers(obj.users);
                } else {
                    self.updateOutput(obj);
                    if (!window_focus) {
                        $.playSound('ding');
                        self.changeTitle(obj.text);
                        setTimeout(function () {
                            $("#soundDiv").remove();
                        }, 1050);
                    }
                }
            }
        };

        webSocket.onclose = function (event) {
            $("#userStatus").text("Connection Closed");
            isConnected(false);
        };
    };

    this.loadUsers = function (arr) {
        $.each(arr, function (index, value) {
            usersList.push(value);
        });
    };

    this.loadHistory = function (hist) {
        $.each(hist, function (index, value) {
            self.updateOutput(value);
        });
    };

    this.updateOutput = function (obj) {
        output.append("<div style='background-color: " + obj.color + "'>" + obj.date + " <b>" + obj.sender + "</b>: " + obj.text + "</div>");
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

    this.changeTitle = function (message) {
        var title = $(document).prop('title');
        if (title.indexOf('>') == -1) {
            $(document).prop('title', +message);
            title = message;
        }

        if (title.indexOf('>>>') == -1) {
            $(document).prop('title', '>' + title);
            setTimeout(self.changeTitle(), 1000);
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