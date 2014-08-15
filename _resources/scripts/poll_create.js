$(document).ready(function() {
    var current_language = $('html').attr('lang') || "de";

    var $datetimepicker = $('.input-datetimepicker');

    $datetimepicker.children('input').attr("data-date-format", "YYYY-MM-DD HH:mm");

    $datetimepicker.datetimepicker({
        language: current_language,
        pick12HourFormat: false,
        icons: {
            time: "fa fa-clock-o",
            date: "fa fa-calendar",
            up: "fa fa-arrow-up",
            down: "fa fa-arrow-down"
        },
        sideBySide: true
    });

    var $startDate, $endDate;
    $datetimepicker.each(function() {
        var $this = $(this);
        if ($this.children('[id$=startDate]').length > 0) {
            $startDate = $this;
        }
        else if ($this.children('[id$=endDate]').length > 0) {
            $endDate = $this;
        }
    });

    if ($startDate) {
        $startDate.on("dp.change", function (e) {
            $endDate.data("DateTimePicker").setMinDate(e.date);
        });
    }
    if ($endDate) {
        $endDate.on("dp.change", function (e) {
            $startDate.data("DateTimePicker").setMaxDate(e.date);
        });
    }

    $datetimepicker.each(function() {
        var picker = $(this).data('DateTimePicker');

        if (picker) {
            $(this).on('click', $.proxy(picker.show, this));
        }
    });
});
