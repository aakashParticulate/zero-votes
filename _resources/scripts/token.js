$(document).ready(function() {
  if (location.pathname.charAt(location.pathname.length-1) == "/") {
    $('[id*=":tokenString"]').val('');
  }
  window.history.replaceState({}, document.title, "/token/");
});
