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

    // --------------------------

    var current_language = $('html').attr('lang') || "de";

    var preview_text, edit_text, delete_text, publish_text;

    switch (current_language) {
        case "en":
            preview_text = "Preview";
            edit_text = "Edit";
            delete_text = "Delete";
            publish_text = "Publish";
            break;
        case "de":
        default:
            preview_text = " Vorschau";
            edit_text = "Editieren";
            delete_text = "Löschen";
            publish_text = "Veröffentlichen";
            break;
    }

    $('.fa-eye').parent().tooltip({
        'placement': 'bottom',
        'title': preview_text
    });

    $('.fa-edit').parent().tooltip({
        'placement': 'bottom',
        'title': edit_text
    });

    $('.fa-trash-o').parent().tooltip({
        'placement': 'bottom',
        'title': delete_text
    });

    $('.fa-thumbs-o-up').parent().tooltip({
        'placement': 'bottom',
        'title': publish_text
    });
});
