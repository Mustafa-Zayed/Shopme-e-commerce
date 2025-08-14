const productForm = $("#productForm");

const extraImageDivs = $("[id ^= 'extraImageDiv']");
let extraImageCount = extraImageDivs.length;

let newProd = $("[id = 'id']").val() === '';

let initialProdImageName = $("#mainImageName").text();

// Get extraImages thumbnails sources and their names
let extraImagesThumbnails = $("[id ^= 'extraThumbnail']");
let extraImagesThumbnailsSrcs = extraImagesThumbnails.map(function () {
    return $(this).attr("src");
})

let extraImagesNames = $("[id ^= 'extraImageName']");
let extraImagesNamesTexts = extraImagesNames.map(function () {
    return $(this).text();
})

$(document).ready(function () {
    $('input[name="extraImage"]').each(function (index){
        $(this).change(function () {
            checkSizeAndShowThumbnailAndAddNewExtraImage(this, $("#extraThumbnail" + index), index);
        })
    })

    $('a[name="linkRemoveExtraImage"]').each(function (index){
        $(this).click(function () {
            removeExtraImageSection(index)
        })
    })

    productForm.on("submit", removeLastExtraImage);
});

function checkSizeAndShowThumbnailAndAddNewExtraImage(fileInput, theThumbnail, index) {
    let fileSize = fileInput.files[0]?.size || 0;
    console.log(fileSize)

    if (fileSize > fileSizeLimit) {
        resetThumbnailSrcAndImageName(theThumbnail, index);
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
        resetThumbnailSrcAndImageName(theThumbnail, index);
        return
    }

    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function () {
        theThumbnail.attr("src", reader.result);
    }

    // display the new image name
    $("#extraImageName" + index).text(file.name);

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
                    <span id="extraImageName${index}"></span>
                </div>

                <div class="mb-2">
                    <img alt="Extra image #${index + 1} preview"
                         class="img-thumbnail"
                         id="extraThumbnail${index}"
                         src="${initialProductThumbnail}">
                </div>

                <div>
                    <input type="file" id="extraImage${index}" name="extraImage"
                           onchange="checkSizeAndShowThumbnailAndAddNewExtraImage(this, $('#extraThumbnail${index}'), ${index})"
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

function resetThumbnailSrcAndImageName(theThumbnail, index){
    // reset thumbnail src to the initial one whatever it's a new or edit product
    if (newProd) {
        theThumbnail.attr("src", initialProductThumbnail);
        // reset the image name
        $("#extraImageName" + index).text('');
    }
    else {
        let initialExtraImageThumbnailSrc = extraImagesThumbnailsSrcs.eq(index)[0];
        theThumbnail.attr("src", initialExtraImageThumbnailSrc);

        // reset the image name
        $("#extraImageName" + index).text(extraImagesNamesTexts.eq(index)[0]);
    }
}

function removeExtraImageSection(index) {
    $("#extraImageDiv" + index).remove();
}

function removeLastExtraImage(){
    $('input[name="extraImage"]').each(function (){
        if (this.value === "")
            this.remove();
    })
}