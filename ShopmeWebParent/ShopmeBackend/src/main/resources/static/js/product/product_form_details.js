const addMoreDetailBtn = $("#addMoreDetail");
const allDetailsDivs = $("#productDetailsDiv")


let detailCount = 1;

$(document).ready(function () {
    addMoreDetailBtn.on("click", addMoreDetail);

    productForm.on("submit", removeEmptyDetails);
});

function addMoreDetail() {
    // approach 2
    // const allDetailsRows = $("[id ^= 'detailRow']"); // must be inside the function as it's not fixed and will increase dynamically
    // const allDetailsRowsCounts = allDetailsRows.length;
    // const nextDivDetailId = allDetailsRowsCounts // we can replace detailCount with nextDivDetailId

    let htmlDetailSection = `
        <div class="row mb-3" id="detailRow${detailCount}">
            <div class="col-4 d-flex align-items-center">
                <label for="name${detailCount}" class="me-2 mb-0">Name:</label>
                <input type="text" class="form-control" id="name${detailCount}" name="detailName" maxlength="255">
            </div>
        
            <div class="col-4 d-flex align-items-center">
                <label for="value${detailCount}" class="me-2 mb-0">Value:</label>
                <input type="text" class="form-control" id="value${detailCount}" name="detailValue" maxlength="255">
            </div>
        </div>
    `
    // approach 2
    // let prevDetailDiv = allDetailsRows.last();
    // let prevDetailDivID = prevDetailDiv.attr("id");

    let htmlLinkRemove = `
        <div class="col-2">
            <a class="btn fa-solid fa-times-circle fa-2x icon-dark" 
            href="javascript:removeDetailSection(${detailCount - 1})"
            title="Remove this detail"
            ></a>
        </div>
    `
    // href="javascript:removeDetailSection('${prevDetailDivID}')" // approach 2

    allDetailsDivs.append(htmlDetailSection);
    $("#detailRow" + (detailCount - 1)).append(htmlLinkRemove);
    // prevDetailDiv.append(htmlLinkRemove); // approach 2

    $("input[name='detailName']").last().focus(); // move the focus to the last row

    detailCount++;
}

function removeDetailSection(index) {
    $("#detailRow" + index).remove();
    // $("#" + index).remove(); // approach 2
}

function removeEmptyDetails(){
    $('input[name="detailName"]').each(function (){
        if (this.value === "")
            this.remove();
    })

    $('input[name="detailValue"]').each(function (){
        if (this.value === "")
            this.remove();
    })
}