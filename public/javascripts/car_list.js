/* global jsconfig, Messages */

$(function () {
    // sanity check, we really won't to run this code if datatable plugin is missing
    if (!jQuery().dataTable) {
        return;
    }

    // Set up data url
    jsconfig.datatables.sAjaxSource = $('#carList').data("url");

    var carList = $('#carList').dataTable(jsconfig.datatables);

    // modify table search input
    $('#carList_wrapper .dataTables_filter').addClass("form-group pull-left");
    $('#carList_wrapper .dataTables_length select').addClass("form-control input-small ");
    $('#carList_wrapper .dataTables_length select').css({display: "block"});
    $('#carList_wrapper .dataTables_length').addClass("pull-right");

    $('.input_search')
        .append('<div class="input-group">' +
            ' <input type="text" id="searchQuery" aria-controls="carList" class="form-control" placeholder="Search...">' +
            ' <span class="input-group-btn">' +
            ' <button id="searchBtn" class="btn btn-primary"><i class="fa fa-search"></i></button>' +
            ' </span>' +
            '</div>');

    // add search field submission handlers
    $('#searchBtn').click(function () {
        carList.fnFilter($('#searchQuery').val());
    });

    $("#searchQuery").keyup(function (event) {
        if (event.keyCode == 13) {
            carList.fnFilter($('#searchQuery').val());
        }
    });

    carList.on('init.dt page.dt draw.dt', function () {
        carList.find(".delete-car").click(function (event, element) {
            var modal = $("#deleteCarModal");

            event.preventDefault();
            modal.load($(this).data("url"), function () {
                modal.find("#remove").click(function () {
                    $.ajax({
                        url: $(this).data("url"),
                        type: "POST"
                    }).done(function (data) {
                        modal.modal("hide");
                        carList.fnDraw();
                        showAlert(data.message, "success");
                    }).fail(function (data) {
                        modal.modal("hide");
                        carList.fnDraw();

                        if (data.message) {
                            showAlert(data.message, "danger");
                        } else {
                            showAlert(Messages.error.unknown.message, "danger");
                        }
                    });
                });
            });

            modal.modal("show");
        });
    });

    function showAlert(message, type) {
        var settings = {
            type: type
        }

        if (type == "success") {
            settings.delay = 3000;
        } else {
            settings.type = 'danger';
            settings.delay = 0;
        }

        $.notify({ message: message }, settings);
    }
});
