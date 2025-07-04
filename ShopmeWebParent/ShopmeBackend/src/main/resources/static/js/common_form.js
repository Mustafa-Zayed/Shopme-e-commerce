let thumbnail = $("#thumbnail");
let prevSrcBeforeChanging = thumbnail.attr("src"); // the initial thumbnail source
console.log("prevSrcBeforeChanging: " + prevSrcBeforeChanging)

// Define the fileSizeLimit variable, assigning it the value of MAX_FILE_SIZE if available.
// If MAX_FILE_SIZE is not declared, use a default value instead.
// This approach avoids the need to declare MAX_FILE_SIZE in every file that uses this JS file.
// 'typeof' is preferred for checking if a variable is defined or not, while !== null only
// checks for the null value, not for undefined or undeclared variables.
let fileSizeLimit = typeof MAX_FILE_SIZE !== "undefined" ? MAX_FILE_SIZE : 102400; // default is 100kb => 1024 * 100

$(document).ready(function () {
    $("#buttonCancel").on("click", function () {
        // we define the moduleURL in the html page, as it's a thymeleaf variable
        // and this js file cannot identify its syntax
        window.location = moduleURL;
    });

    $("#imageFile").change(function () {
        checkSizeAndShowThumbnail(this, thumbnail, prevSrcBeforeChanging);
    })
});

function checkSizeAndShowThumbnail(fileInput, theThumbnail, thePrevSrcBeforeChanging) {
    let fileSize = fileInput.files[0]?.size || 0;
    console.log(fileSize)

    if (fileSize > fileSizeLimit) {
        theThumbnail.attr("src", thePrevSrcBeforeChanging); // reset the thumbnail
        fileInput.value = ""; // reset the input value to 'No file chosen' instead of the big file name
        fileInput.setCustomValidity("Image size must be less than " + fileSizeLimit / 1024 + "KB! =)");
        fileInput.reportValidity();
    } else {
        fileInput.setCustomValidity("");
        showImageThumbnail(fileInput, theThumbnail, thePrevSrcBeforeChanging);
    }
}

function showImageThumbnail(fileInput, theThumbnail, thePrevSrcBeforeChanging) {
    console.log(fileInput.files)
    let file = fileInput.files[0];

    if (file == null) { // If no image selected, return to the original one
        theThumbnail.removeAttr("src");
        theThumbnail.attr("src", thePrevSrcBeforeChanging);
        return
    }

    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function () {
        theThumbnail.attr("src", reader.result);
        // console.log("reader.result: " + reader.result)
    }
}


function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").html(message); // Use .html() to allow HTML tags in the message

    // jQuery's .show() method, makes the modal visible without properly initializing it as
    // a Bootstrap modal. To fix this, you need to initialize the modal using Bootstrap's
    // JavaScript API instead of just making it visible with .show().
    // This ensures that the modal is displayed and can be closed properly using the
    // data-bs-dismiss attribute.
    const modalElement = $("#modalDialog");
    const modalInstance = new bootstrap.Modal(modalElement); // Create a Bootstrap modal instance
    modalInstance.show();
}

function showWarningModal(message) {
    showModalDialog("Warning", message);
}

function showErrorModal(message) {
    showModalDialog("Error", message);
}