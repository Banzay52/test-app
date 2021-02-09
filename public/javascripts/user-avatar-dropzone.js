
$(document).ready(function() {
    function initUserAvatarDropzone() {
        var previewNode = document.querySelector("#template");
        previewNode.id = "";
        var previewTemplate = previewNode.parentNode.innerHTML;

        var userAvatarDropzone = new Dropzone(document.body, { // Make the whole body a dropzone
            url: "/upload",
            previewTemplate: previewTemplate,
            acceptedFiles: "image/jpeg,image/png,image/jpg",
            maxFilesize: 5,
            thumbnailWidth: 192,
            thumbnailHeight: 192,
            autoQueue: false, // Make sure the files aren't queued until manually added
            previewsContainer: "#previews", // Define the container to display the previews
            clickable: "#dropzoneForUserAvatar", // Define the element that should be used as click trigger to select files.
            maxFiles: 1,
            resizeWidth: 192,
            resizeHeight: 192,
            init: function () {
                this.on('addedfile', function () {
                    if (this.files.length > 1) {
                        this.removeFile(this.files[1]);
                    }
                });
            }
        });

        var element = document.getElementById("dropzoneForUserAvatar");
        element.setAttribute("style", "cursor:pointer;");
        element.setAttribute("data-toggle", "tooltip");
        element.setAttribute("data-placement", "bottom");
        element.setAttribute("title", document.getElementById("hint").getAttribute("data-hint"));

        $('[data-toggle="tooltip"]').tooltip();

        userAvatarDropzone.on("addedfile", function (file) {
            previewNode.parentNode.removeChild(previewNode);
            file.previewElement.querySelector(".start").onclick = function () {
                userAvatarDropzone.enqueueFile(file);
            };
            document.querySelector("#initUpload").style.display = "";
            $("#delete-avatar-image").hide();
        });

        userAvatarDropzone.on("sending", function () {
            document.querySelector("#initUpload").style.display = "none";
        });

        userAvatarDropzone.on("success", function () {
            var message = document.getElementById("hint").getAttribute("data-success");
            $(".dz-image-preview").append("<div class='text text-success'>" + message + "</div>");
            setTimeout(reload, 2000);
            function reload(){
                location.reload(true);
            }
        });

        document.querySelector(".start").onclick = function () {
            userAvatarDropzone.enqueueFiles(userAvatarDropzone.getFilesWithStatus(Dropzone.ADDED));
        };

        $("body").on("click", ".cancel", function () {
            location.reload(true);
        });

        var allowSubmit = false;
        $("body").on("click", "#delete-image-link", function (e) {
            if (!allowSubmit) {
                e.preventDefault();
                e.stopPropagation();
                if (confirm($("#delete-image-link").data("confirm-question"))) {
                    allowSubmit = true;
                    $(this).trigger('click');
                }
            }
        });


     }

    initUserAvatarDropzone();
});
