var ZVotes = {};

ZVotes.getQueryString = function(param) {
    var str = window.location.search;
    var objURL = {};
    str.replace(
        new RegExp( "([^?=&]+)(=([^&]*))?", "g" ),
        function( $0, $1, $2, $3 ){
            objURL[ $1 ] = $3;
        }
    );
    return objURL[param];
};

$(document).ready(function() {
    toastr.options = {
        "closeButton": true
    };

	var messages_displayed = [];
	$('.notification-fatal').each(function() {
		if (messages_displayed.indexOf('fatal_'+$(this).html()) == -1) {
			toastr.error($(this).html());
			messages_displayed.push('fatal_'+$(this).html());
		}
	});
	$('.notification-error').each(function() {
		if (messages_displayed.indexOf('error_'+$(this).html()) == -1) {
			toastr.error($(this).html());
			messages_displayed.push('error_'+$(this).html());
		}
	});
	$('.notification-warning').each(function() {
		if (messages_displayed.indexOf('warning_'+$(this).html()) == -1) {
			toastr.warning($(this).html());
			messages_displayed.push('warning_'+$(this).html());
		}
	});
	$('.notification-info').each(function() {
		if (messages_displayed.indexOf('info_'+$(this).html()) == -1) {
			toastr.info($(this).html());
			messages_displayed.push('info_'+$(this).html());
		}
	});

  // --------------------------

  var current_language = $('html').attr('lang') || "de";

  var preview_text, edit_text, delete_text, publish_text, results_text, participants_text, questions_text;

  switch (current_language) {
      case "en":
          preview_text = "Preview";
          edit_text = "Edit";
          delete_text = "Delete";
          publish_text = "Publish";
          results_text = "Results";
          participants_text = "Participants";
      		recipients_text = "Recipients";
      		organizers_text = "Organizers";
          questions_text = "Questions";
          break;
      case "de":
      default:
          preview_text = " Vorschau";
          edit_text = "Editieren";
          delete_text = "Löschen";
          publish_text = "Veröffentlichen";
          results_text = "Ergebnisse";
          participants_text = "Teilnehmer";
      		recipients_text = "Empfänger";
      		organizers_text = "Organisatoren";
          questions_text = "Fragen";
          break;
  }

  $('.zvotes-preview-btn').parent().tooltip({
      'placement': 'bottom',
      'title': preview_text
  });

  $('.zvotes-edit-btn').parent().tooltip({
      'placement': 'bottom',
      'title': edit_text
  });

  $('.zvotes-delete-btn').parent().tooltip({
      'placement': 'bottom',
      'title': delete_text
  });

  $('.zvotes-publish-btn').parent().tooltip({
      'placement': 'bottom',
      'title': publish_text
  });

  $('.zvotes-results-btn').parent().tooltip({
      'placement': 'bottom',
      'title': results_text
  });

  $('.zvotes-participants-btn').parent().tooltip({
      'placement': 'bottom',
      'title': participants_text
  });

  $('.zvotes-recipients-btn').parent().tooltip({
      'placement': 'bottom',
      'title': recipients_text
  });

  $('.zvotes-questions-btn').parent().tooltip({
      'placement': 'bottom',
      'title': questions_text
  });

  $('.zvotes-organizers-btn').parent().tooltip({
      'placement': 'bottom',
      'title': organizers_text
  });

	
	$('.zvotes-delete-btn').each(function() {
		$(this).parent().each(function() {
			var handlerFunction = $(this)[0].onclick;
			var $modal = $('#deleteModal');
			$(this).removeAttr('onclick').on('click', function(e) {
				e.preventDefault();
				$modal.find('.btn-danger').on('click', function(e) {
					e.preventDefault();
					handlerFunction();
					$modal.modal('hide');
				});
				$modal.modal('show');
			});
		});
	});
	
	$('.progress .progress-bar').progressbar({
		display_text: 'center',
		use_percentage: false
	});
	
	$('.option-real').on('click', function(e) {
		var abstention = true;
		var checked = 0;
		$item=$(this).closest('.item');
		$item.find('.option-real').each(function() {
			 if ($(this)[0].checked == true) { abstention = false; checked += 1; }
		});
		$item.find('.option-abstention')[0].checked= abstention;
		if (checked > parseInt($item.attr('data-m'))) {
			e.preventDefault();
		}
	});
	$('.option-abstention').on('click', function(e) {
    $(this)[0].checked = true;
		$item=$(this).closest('.item');
		$item.find('.option-real').each(function() {
      $(this)[0].checked = false;
		});
	});
  
  $(document).ready(function() {
  	$('.option-abstention').each(function() {
      $(this)[0].checked = true;
    });
  	$('.option-real').each(function() {
      $(this)[0].checked = false;
    });
    $('.freetext-input').val(''));
  });
});
