$(document).ready(function () {
    $("#countrySelect").on("change", function () {
        const countryId = $(this).val();
        $("#stateList").empty();
        $("#state").val("");

        if (!countryId) return;

        let url = statesByCountryUrl + countryId;
        $.get(url, function (states) {
            $.each(states, function (index, state) {
                $("#stateList").append(
                    $("<option>").val(state.name).text(state.name)
                );
            });
        });
    });
});

function checkEmailUniqueness() {
    return new Promise((resolve, reject) => {
        const customerEmail = $("#email").val();
        const url = checkEmailUniquenessUrl;
        const csrfValue = $("input[name='_csrf']").val();

        $.post(url, { email: customerEmail, _csrf: csrfValue })
            .done(function (response) {
                if (response === true) {
                    resolve(true);
                } else if (response === false) {
                    showWarningModal(
                        `There is another customer having the email: <b>${customerEmail}</b>`
                    );
                    resolve(false);
                } else {
                    showErrorModal("Unknown response from the server.");
                    reject();
                }
            })
            .fail(function () {
                showErrorModal("Could not connect to server.");
                reject();
            });
    });
}

function checkFullNameUniqueness() {
    return new Promise((resolve, reject) => {
        const customerId = $("#id").val();
        const firstName = $("#firstName").val();
        const lastName = $("#lastName").val();
        const fullName = firstName + " " + lastName;

        const url = checkFullNameUniquenessUrl;
        const csrfValue = $("input[name='_csrf']").val();

        $.post(url, { id: customerId, fullName: fullName, _csrf: csrfValue })
            .done(function (response) {
                if (response === true) {
                    resolve(true);
                } else if (response === false) {
                    showWarningModal(
                        `There is another customer having the full name: <b>${fullName}</b>`
                    );
                    resolve(false);
                } else {
                    showErrorModal("Unknown response from the server.");
                    reject();
                }
            })
            .fail(function () {
                showErrorModal("Could not connect to server.");
                reject();
            });
    });
}

function checkPhoneNumberIntegrity() {
    return new Promise((resolve, reject) => {
        const phoneNumber = $("#phoneNumber").val();
        const url = checkPhoneNumberIntegrityUrl;
        const csrfValue = $("input[name='_csrf']").val();

        $.post(url, { phoneNumber: phoneNumber, _csrf: csrfValue })
            .done(function (response) {
                if (response === true) {
                    resolve(true);
                } else if (response === false) {
                    showWarningModal(
                        `This phone number is not valid: <b>${phoneNumber}</b>`
                    );
                    resolve(false);
                } else {
                    showErrorModal("Unknown response from the server.");
                    reject();
                }
            })
            .fail(function () {
                showErrorModal("Could not connect to server.");
                reject();
            });
    });
}

function checkPhoneNumberUniqueness() {
    return new Promise((resolve, reject) => {
        const customerId = $("#id").val();
        const phoneNumber = $("#phoneNumber").val();
        const url = checkPhoneNumberUniquenessUrl;
        const csrfValue = $("input[name='_csrf']").val();

        $.post(url, { id: customerId, phoneNumber: phoneNumber, _csrf: csrfValue })
            .done(function (response) {
                if (response === true) {
                    resolve(true);
                } else if (response === false) {
                    showWarningModal(
                        `There is another customer having the phone number: <b>${phoneNumber}</b>`
                    );
                    resolve(false);
                } else {
                    showErrorModal("Unknown response from the server.");
                    reject();
                }
            })
            .fail(function () {
                showErrorModal("Could not connect to server.");
                reject();
            });
    });
}

async function validateAndSubmit(form) {

    if (!await checkEmailUniqueness()) return;
    if (!await checkFullNameUniqueness()) return;
    if (!await checkPhoneNumberIntegrity()) return;
    if (!await checkPhoneNumberUniqueness()) return;

    // All checks passed
    form.submit();
}


function checkPasswordMatch(confirmPassword) {
    if (confirmPassword.value !== $("#password").val())
        confirmPassword.setCustomValidity("Passwords do not match!");
    else
        confirmPassword.setCustomValidity("");
}