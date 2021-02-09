$(document).ready(function() {
    function bindPathParamFromSelect() {
        $('#selectRowValue, #groupBrand').change(function (e) {
            e.preventDefault();
            window.location = $(this).children('option:selected').data('url');
        });
    }

    function initSearch() {
        $('#crudSearchTerm').change(function () {
            setTimeout(sendForm, 1000);
            function sendForm(){
                $('#crudSearchTerm').closest('form').submit();
            }

        });
    }

    bindPathParamFromSelect();
    initSearch()
});
