$(document).ready(function () {
    customizeDropDownMenu();
})

function customizeDropDownMenu() {
    $('.navbar .dropdown').hover(
        function () {
            $(this).find('.dropdown-menu').stop(true, true).delay(250).slideDown();
        },
        function () {
            $(this).find('.dropdown-menu').stop(true, true).delay(100).slideUp();
        }
    );

    $('.dropdown > a').click(function () {
        location.href = this.href;
    })
}