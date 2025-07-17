const brandSelect = $("#brand");
const categorySelect = $("#category");

$(document).ready(function () {
    // Initial call (for default brand selected when the page loads)
    getCategories();

    // Update categories on change
    brandSelect.on("change", getCategories);

    $('.content').richText();
});

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