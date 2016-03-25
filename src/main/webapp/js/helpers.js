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
            setTimeout(function () {
                $("#soundDiv").remove();
            }, 1100);

            return $(
                '<audio id="soundDiv" autoplay="autoplay" style="display:none;">'
                + '<source src="audio/' + arguments[0] + '.mp3" />'
                + '<embed src="audio/' + arguments[0] + '.mp3" hidden="true" autostart="true" loop="false" class="playSound" />'
                + '</audio>'
            ).appendTo('body');
        }
    });
})(jQuery);