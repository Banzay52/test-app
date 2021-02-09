jQuery(document).ready(function($) {
    $('#confirm-button').click(function(e) {
        return window.confirm($('#confirm-message').text());
    });
});
