let thumbnail = $("#thumbnail");
let prevSrcBeforeChanging = thumbnail.attr("src"); // the initial thumbnail source
console.log("prevSrcBeforeChanging: " + prevSrcBeforeChanging)

$(document).ready(function () {
    $("#buttonCancel").on("click", function () {
        // we define the moduleURL in the html page, as it's a thymeleaf variable
        // and this js file cannot identify its syntax
        window.location = moduleURL;
    });

    $("#imageFile").change(function () {
        let fileSize = this.files[0]?.size || 0;
        console.log(fileSize)

        if (fileSize > 102400) { // 1024 * 100
            this.setCustomValidity("Image size must be less than 100KB! =)");
            this.reportValidity();
        } else {
            this.setCustomValidity("");
            showImageThumbnail(this);
        }
    })
});

function showImageThumbnail(fileInput) {
    console.log(fileInput.files)
    let file = fileInput.files[0];

    if (file == null) { // If no image selected, return to the original one
        thumbnail.removeAttr("src");
        thumbnail.attr("src", prevSrcBeforeChanging);
        return
    }

    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function () {
        thumbnail.attr("src", reader.result);
        // console.log("reader.result: " + reader.result)
    }
}