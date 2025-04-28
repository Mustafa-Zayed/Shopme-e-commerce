/*Approach one*/
// let deletedId;
// function showDeleteModal(element) {
//     const userId = element.getAttribute('data-user-id');
//     const userFName = element.getAttribute('data-user-fName');
//     const userLName = element.getAttribute('data-user-lName');
//     console.log(userId, userFName, userLName);
//     deletedId = userId;
//
//     $("#modalBody").html(`Are you sure you want to delete this user ID ${userId}: ${userFName} ${userLName}`)
//     const modalElement = $("#modalDialog");
//     const modalInstance = new bootstrap.Modal(modalElement);
//     modalInstance.show();
// }
//
// function confirmDelete(){
//     let url = "[[@{/users/delete/}]]" + deletedId;
//     console.log('deletedId: ' + deletedId);
//     console.log('url: ' + url);
//     window.location.href = url;
// }

/*Approach two*/
$(document).ready(function () {
    $(".link-delete").on('click', function (e) {
        e.preventDefault();
        const deleteLink = $(this);
        const id = deleteLink.attr('entityId')
        $("#yesButton").attr('href', deleteLink.attr('href')); // should remove data-bs-dismiss from the modal yes button
        $("#modalBody").html(`Are you sure you want to delete this ${entity} ID: ${id}?`);
        const modalElement = $("#modalDialog");
        const modalInstance = new bootstrap.Modal(modalElement);
        modalInstance.show();
    });
})

function clearSearch() {
    window.location.href = path;
}
