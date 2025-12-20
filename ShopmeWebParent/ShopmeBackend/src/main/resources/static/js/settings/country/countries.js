let buttonLoadCountries;
let dropDownCountryList;
let buttonAddCountry;
let buttonUpdateCountry;
let buttonDeleteCountry;
let fieldCountryName;
let fieldCountryCode;
let countryNameLabel;

$(document).ready(function() {
    buttonLoadCountries = $('#buttonLoadCountries')
    dropDownCountryList = $('#dropDownCountryList')
    buttonAddCountry = $('#buttonAddCountry')
    buttonUpdateCountry = $('#buttonUpdateCountry')
    buttonDeleteCountry = $('#buttonDeleteCountry')
    fieldCountryName = $('#fieldCountryName')
    fieldCountryCode = $('#fieldCountryCode')
    countryNameLabel = $('#countryNameLabel')

    buttonLoadCountries.click(function() {
        loadCountries();
    });

    dropDownCountryList.change(function () {
        changeFormStateToSelectedCountry();
    })

    buttonAddCountry.click(function() {
        if (buttonAddCountry.text() === "New") {
            changeFormStateToNew();
        } else if (fieldCountryName.val().trim() === "") {
            showMessage('Please enter a country name', "alert alert-danger");
            fieldCountryName.focus();
        } else if (fieldCountryCode.val().trim() === "") {
            showMessage('Please enter a country code', "alert alert-danger");
            fieldCountryCode.focus();
        } else
            addCountry();
    });

    buttonUpdateCountry.click(function (){
        if (fieldCountryName.val().trim() === "") {
            showMessage('Please enter a country name', "alert alert-danger");
            changeFormStateToNew();
        }
        updateCountry();
    })

    buttonDeleteCountry.click(function() {
        deleteCountry();
    });
});

function showMessage(text, alertClass  = "alert alert-info") {
    const $msg = $('#message');
    $msg.removeClass().addClass(alertClass)
        .text(text)
        .fadeIn(200, function() {
        setTimeout(() => $msg.fadeOut(1000), 3000);
    });
}

// Click on list item
function changeFormStateToSelectedCountry() {
    const selectedCountry = dropDownCountryList.find('option:selected');
    const value = selectedCountry.val();
    const name = selectedCountry.text();
    const id = value.split('-')[0];
    const code = value.split('-')[1];

    // Not working properly
    // const id = selectedCountry.data('id');
    // const code = selectedCountry.data('code');

    // Enable buttons & set text to "New"
    buttonAddCountry.text('New');
    buttonUpdateCountry.prop('disabled', false);
    buttonDeleteCountry.prop('disabled', false);

    // Set form values
    fieldCountryName.val(name);
    fieldCountryCode.val(code);
    countryNameLabel.text('Selected Country:');
    $('#countryForm').attr('country-id', id);
}

// Change form state to new country
function changeFormStateToNew() {
    // Disable buttons & set text to "Add"
    buttonAddCountry.text('Add');
    buttonUpdateCountry.prop('disabled', true);
    buttonDeleteCountry.prop('disabled', true);

    // Set form values
    fieldCountryName.val('').focus();
    fieldCountryCode.val('');
    countryNameLabel.text('Country Name:');
    $('#countryForm').removeAttr('country-id');
}

// Load countries
function loadCountries() {
    $.get(loadUrl, function(data) {
        dropDownCountryList.empty();

        data.forEach(country => {
            let optionValue = country.id + "-" + country.code;
            dropDownCountryList.append(
                `<option value="${optionValue}" class="country-option" data-id="${country.id}" data-code="${country.code}">${country.name}</option>`
            );
        });
    }).done(function() {
        buttonLoadCountries.val("Refresh Country List");
        showMessage('All countries have been loaded');
    }).fail(function() {
        buttonLoadCountries.val("Load Country List");
        showMessage('Error: Could not connect to server', "alert alert-danger");
    });
}

function selectNewlyAddedCountry(response) {
    const optionValue = response.id + "-" + response.code;

    $("<option>").val(optionValue)
        .text(response.name)
        .attr('class', 'country-option')
        .attr('data-id', response.id)
        .attr('data-code', response.code)
        .appendTo(dropDownCountryList);

    $("#dropDownCountryList option[value='" + optionValue + "']").prop('selected', true);
    // $(`#dropDownCountryList option[value="${optionValue}"]`).prop('selected', true);

    fieldCountryName.val('').focus();
    fieldCountryCode.val('');
}

function addCountry() {
    const name = fieldCountryName.val();
    const code = fieldCountryCode.val();
    const jsonData = JSON.stringify({name: name, code: code});

    $.ajax({
        type: 'POST',
        url: saveUrl,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: jsonData,
        contentType: 'application/json',
    }).done(function(response) {
        showMessage('The new country has been added successfully')
        selectNewlyAddedCountry(response);
    }).fail(function() {
        showMessage('Error: Could not connect to server', "alert alert-danger");
    });
}

// Update Country
function updateCountry() {
    const id = $('#countryForm').attr('country-id');
    const name = fieldCountryName.val();
    const code = fieldCountryCode.val();

    const jsonData = JSON.stringify({ id: id, name: name, code: code  });

    $.ajax({
        type: 'POST',
        url: saveUrl,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: jsonData,
        contentType: 'application/json',
    }).done(function(response) {
        // Update the selected option in the dropdown
        const optionValue = response.id + "-" + response.code;
        let selectedCountry = $("#dropDownCountryList option:selected");
        selectedCountry
            .val(optionValue)
            .text(response.name)
            .attr('data-code', response.code);

        changeFormStateToNew();
        showMessage('The selected country has been updated successfully')
    }).fail(function() {
        showMessage('Error: Could not connect to server', "alert alert-danger");
    });
}

// Delete Country
function deleteCountry() {
    const id = $('#countryForm').attr('country-id');

    $.ajax({
        url: deleteUrlBase + id,
        type: 'DELETE',
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
    }).done(function() {
        // Remove the selected option in the dropdown
        let selectedCountry = $("#dropDownCountryList option:selected");
        selectedCountry.remove();

        changeFormStateToNew();
        showMessage('The selected country has been deleted successfully')
    }).fail(function() {
        showMessage('Error: Could not connect to server', "alert alert-danger");
    });
}