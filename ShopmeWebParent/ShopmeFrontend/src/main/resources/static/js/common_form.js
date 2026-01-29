$(document).ready(function () {
    $("#buttonCancel").on("click", function () {
        // we define the moduleURL in the html page, as it's a thymeleaf variable
        // and this js file cannot identify its syntax
        window.location = moduleURL;
    });
    })

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").html(message); // Use .html() to allow HTML tags in the message

    // jQuery's .show() method, makes the modal visible without properly initializing it as
    // a Bootstrap modal. To fix this, you need to initialize the modal using Bootstrap's
    // JavaScript API instead of just making it visible with .show().
    // This ensures that the modal is displayed and can be closed properly using the
    // data-bs-dismiss attribute.
    const modalElement = $("#dialog");
    const modalInstance = new bootstrap.Modal(modalElement); // Create a Bootstrap modal instance
    modalInstance.show();
}

function showWarningModal(message) {
    showModalDialog("Warning", message);
}

function showErrorModal(message) {
    showModalDialog("Error", message);
}