const brandSelect = $("#brand");
const categorySelect = $("#category");

let extraImageCount = 1;

$(document).ready(function () {
    // Initial call (for default brand selected when the page loads)
    getCategories();

    // Update categories on change
    brandSelect.on("change", getCategories);

    $('.content').richText();

    $('input[name="extraImage"]').each(function (index){
        $(this).change(function () {
            checkSizeAndShowThumbnail(this, $("#extraThumbnail0"), index);
        })
    })
});

function checkSizeAndShowThumbnail(fileInput, theThumbnail, index) {
    let fileSize = fileInput.files[0]?.size || 0;
    console.log(fileSize)

    if (fileSize > fileSizeLimit) {
        theThumbnail.attr("src", initialProductThumbnail); // reset the thumbnail
        fileInput.value = ""; // reset the input value to 'No file chosen' instead of the big file name
        fileInput.setCustomValidity("Image size must be less than " + fileSizeLimit / 1024 + "KB! =)");
        fileInput.reportValidity();
    } else {
        fileInput.setCustomValidity("");
        showImageThumbnailAndAddNextExtraImageSection(fileInput, theThumbnail, index);
    }
}

function showImageThumbnailAndAddNextExtraImageSection(fileInput, theThumbnail, index) {
    console.log(fileInput.files)
    let file = fileInput.files[0];

    if (file == null) { // If no image selected, return to the original one
        theThumbnail.removeAttr("src");
        theThumbnail.attr("src", initialProductThumbnail);

        return
    }

    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function () {
        theThumbnail.attr("src", reader.result);
    }

    // This code has a logic issue as it's using the array length. It fails when images are removed
    // because the length decreases, but the index in the new section remains the same.
    // Instead of using the length, we should track the maximum index of existing extra images
    // const isLast = (index - 1) === $('input[name="extraImage"]').length - 1;
    //////

    // // Add a new extra image only if we've chosen the last image (index === maxIndex)
    // // Find the maximum index of existing extra images
    // let maxIndex = -1;
    // $('input[name="extraImage"]').each(function () {
    //     const currentId = $(this).attr('id');
    //     const currentIndex = parseInt(currentId.replace('extraImage', ''));
    //     if (currentIndex > maxIndex) {
    //         maxIndex = currentIndex;
    //     }
    // });
    //
    // console.log("maxIndex: " + maxIndex);
    // console.log("index: " + index);
    //
    // const isLast = index === maxIndex
    //
    // if (!isLast)
    //     return;

    console.log("index: " + index)
    console.log("extraImageCount - 1: " + (extraImageCount - 1))

    // another better approach
    const isLast = index === (extraImageCount - 1)

    if (!isLast)
        return;

    AddNextExtraImageSection(index + 1);
}

function AddNextExtraImageSection(index) {
    let htmlExtraImage = `
            <div class="col border border-secondary rounded p-2 m-3" id="extraImageDiv${index}">
                <div id="extraImageHeader${index}"> 
                    <label for="extraImage${index}" class="mb-1">Extra Image #${index + 1}:</label>
                </div>

                <div class="mt-2">
                    <img alt="Extra image #${index + 1} preview"
                         class="img-thumbnail"
                         id="extraThumbnail${index}"
                         src="${initialProductThumbnail}">
                </div>

                <div>
                    <input type="file" id="extraImage${index}" name="extraImage"
                           onchange="checkSizeAndShowThumbnail(this, $('#extraThumbnail${index}'), ${index})"
                           accept="image/png, image/jpeg"
                           class="img-thumbnail">
                </div>
            </div>
    `
    let htmlLinkRemove = `
        <a class="btn fa-solid fa-times-circle fa-2x float-end icon-dark" 
        href="javascript:removeExtraImageSection(${index - 1})"
        title="Remove this image"
        ></a>
    `

    $("#productImagesDiv").append(htmlExtraImage);
    $("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);
    extraImageCount++;

}

function removeExtraImageSection(index) {
    $("#extraImageDiv" + index).remove();
}

function getCategories() {
    let brandId = brandSelect.val(); // brandSelect.find("option:selected").val();
    let url = brandModuleURL + "/" + brandId + "/categories";

    $.get(
        url,
        function (response) {
            categorySelect.empty();
            $.each(response, function (index, category) {
                $("<option>")
                    .val(category.id)
                    .text(category.name)
                    .appendTo(categorySelect);
                // categorySelect.append($("<option></option>")
                //     .val(category.id)
                //     .text(category.name));
            });
        });
}

function checkUniqueName(form) {
    let productName = $("#name").val();
    let productId = $("#id").val();
    let csrfValue = $("input[name='_csrf']").val(); // the server generates a unique CSRF token and sends it to the client (e.g., embedded in the HTML or as part of a response header).
    let params = {name: productName, id: productId, _csrf: csrfValue};

    $.post(
        productUniqueNameUrl,
        params,
        function (response) {
            if (response === "OK")
                // alert("OK")
                form.submit();
            else if (response === "Duplicated")
                showWarningModal(`There is another product having the name: <b>${productName}</b>`);
            else
                showErrorModal("Unknown response from the server.");
        }
    ).fail(function () { // in case it fails to connect to the server (i.e '/products/check_unique_name' API)
            showErrorModal("Could not connect to server.")
        }
    )

    return false;
}