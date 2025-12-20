$(document).ready(function () {
    customizeDropDownMenu();

    customizeTabs();
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

function customizeTabs() {
    // JavaScript to enable link to tab
    let url = document.location.toString();
    let hash = url.split('#')[1];
    // console.log('url: ' + url);
    // console.log('hash: ' + hash);

    if (url.match('#')) {
        // $('.nav-tabs a[href="#' + hash + '"]').tab('show');
        $(`.nav-tabs a[href="#${hash}"]`).tab('show');
    }

    $('.nav-tabs a').on('shown.bs.tab', function (e) {
        window.location.hash = e.target.hash;
    })
}