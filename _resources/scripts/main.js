$(document).ready(function() {
    toastr.options = {
        "closeButton": true
    };

	$('.notification-fatal').each(function() {
		toastr.error($(this).html());
	});
	$('.notification-error').each(function() {
		toastr.error($(this).html());
	});
	$('.notification-warning').each(function() {
		toastr.warning($(this).html());
	});
	$('.notification-info').each(function() {
		toastr.info($(this).html());
	});
});
