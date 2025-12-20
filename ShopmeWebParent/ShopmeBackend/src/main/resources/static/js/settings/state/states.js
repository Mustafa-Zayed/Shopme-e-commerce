// Use different variable names and ids to remove the mess up between countries and states tabs
let buttonLoadCountries4StatesTab;
let dropDownCountryList4StatesTab;
let dropDownStateList;
let buttonAddState;
let buttonUpdateState;
let buttonDeleteState;
let fieldStateName;
let stateNameLabel;

$(document).ready(function() {
    buttonLoadCountries4StatesTab = $('#buttonLoadCountries4StatesTab')
    dropDownCountryList4StatesTab = $('#dropDownCountryList4StatesTab')
    dropDownStateList = $('#dropDownStateList')
    buttonAddState = $('#buttonAddState')
    buttonUpdateState = $('#buttonUpdateState')
    buttonDeleteState = $('#buttonDeleteState')
    fieldStateName = $('#fieldStateName')
    stateNameLabel = $('#stateNameLabel')

    buttonLoadCountries4StatesTab.click(function() {
        loadCountries4StatesTab();
    });

    dropDownCountryList4StatesTab.change(function () {
        changeFormStateToNew4StatesTab();
        loadStates();
    })

    dropDownStateList.change(function () {
        changeFormStateToSelectedState();
    })

    buttonAddState.click(function() {
        if (buttonAddState.text() === "New") {
            changeFormStateToNew4StatesTab();
        } else if (fieldStateName.val().trim() === "") {
            showMessage4StatesTab('Please enter a state name', "alert alert-danger");
            changeFormStateToNew4StatesTab();
        } else if (dropDownCountryList4StatesTab.val() === null) {
            showMessage4StatesTab('Please select a country', "alert alert-danger");
            changeFormStateToNew4StatesTab();
        } else
            addState();
    });

    buttonUpdateState.click(function () {
        if (fieldStateName.val().trim() === "") {
            showMessage4StatesTab('Please enter a state name', "alert alert-danger");
            changeFormStateToNew4StatesTab();
        }
        else
            updateState();
    })

    buttonDeleteState.click(function () {
        deleteState();
    })
});

function showMessage4StatesTab(text, alertClass  = "alert alert-info") {
    const $msg = $('#stateMessage');
    $msg.removeClass().addClass(alertClass)
        .text(text)
        .fadeIn(200, function() {
        setTimeout(() => $msg.fadeOut(1000), 3000);
    });
}

// Load countries
function loadCountries4StatesTab() {
    $.get(loadUrl, function(data) {
        dropDownCountryList4StatesTab.empty();
        dropDownStateList.empty();

        data.forEach(country => {
            let optionValue = country.id + "-" + country.code;
            dropDownCountryList4StatesTab.append(
                `<option value="${optionValue}" class="country-option" data-id="${country.id}" data-code="${country.code}">${country.name}</option>`
            );
        });
    }).done(function() {
        buttonLoadCountries4StatesTab.val("Refresh Country List");
        showMessage4StatesTab('All countries have been loaded');
    }).fail(function() {
        buttonLoadCountries4StatesTab.val("Load Country List");
        showMessage4StatesTab('Error: Could not connect to server', "alert alert-danger");
    });
}

// Load states
function loadStates() {
    const selectedCountry = dropDownCountryList4StatesTab.find('option:selected');
    const value = selectedCountry.val();
    const countryId = value.split('-')[0];

    $.get(loadStateUrl + countryId, function(data) {
        dropDownStateList.empty();

        data.forEach(state => {
            let optionValue = state.id;
            dropDownStateList.append(
                `<option value="${optionValue}" class="state-option"">${state.name}</option>`
            );
        });
    });
}

// Change form state to new state
function changeFormStateToNew4StatesTab() {
    // Disable buttons & set text to "Add"
    buttonAddState.text('Add');
    buttonUpdateState.prop('disabled', true);
    buttonDeleteState.prop('disabled', true);

    // Set form values
    fieldStateName.val('').focus();
    stateNameLabel.text('State Name:');
}

// Click on list state item
function changeFormStateToSelectedState() {
    const selectedState = dropDownStateList.find('option:selected');
    const name = selectedState.text();

    // Enable buttons & set text to "New"
    buttonAddState.text('New');
    buttonUpdateState.prop('disabled', false);
    buttonDeleteState.prop('disabled', false);

    // Set form values
    fieldStateName.val(name);
    stateNameLabel.text('Selected State:');
}

function selectNewlyAddedState(response) {
    const optionValue = response.id;

    $("<option>").val(optionValue)
        .text(response.name)
        .appendTo(dropDownStateList);

    $("#dropDownStateList option[value='" + optionValue + "']").prop('selected', true);
    // $(`#dropDownStateList option[value="${optionValue}"]`).prop('selected', true);

    fieldStateName.val('').focus();
}

function addState() {
    const stateName = fieldStateName.val();

    const selectedCountry = dropDownCountryList4StatesTab.find('option:selected');
    const value = selectedCountry.val();
    const countryName = selectedCountry.text();
    const countryId = value.split('-')[0];
    const countryCode = value.split('-')[1];

    const jsonData = JSON.stringify(
        {
            name: stateName,
            country: {
                id: countryId,
                name: countryName,
                code: countryCode
            }
        });

    $.ajax({
        type: 'POST',
        url: saveStateUrl,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: jsonData,
        contentType: 'application/json',
    }).done(function(response) {
        showMessage4StatesTab('The new state has been added successfully')
        selectNewlyAddedState(response);
    }).fail(function() {
        showMessage4StatesTab('Error: Could not connect to server', "alert alert-danger");
    });
}

// Update State
function updateState() {
    const selectedState = dropDownStateList.find('option:selected');
    const stateId = selectedState.val();
    const stateName = fieldStateName.val(); // selectedState.text() not correct as it returns the value of the option not the text user typed

    const selectedCountry = dropDownCountryList4StatesTab.find('option:selected');
    const value = selectedCountry.val();
    const countryName = selectedCountry.text();
    const countryId = value.split('-')[0];
    const countryCode = value.split('-')[1];

    const jsonData = JSON.stringify(
        {
            id: stateId,
            name: stateName,
            country: {
                id: countryId,
                name: countryName,
                code: countryCode
            }
        });

    $.ajax({
        type: 'POST',
        url: saveStateUrl,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: jsonData,
        contentType: 'application/json',
    }).done(function(response) {
        // Update the selected option in the dropdown
        const optionValue = response.id;
        let selectedState = $("#dropDownStateList option:selected");
        selectedState
            .val(optionValue)
            .text(response.name);

        changeFormStateToNew4StatesTab();
        showMessage4StatesTab('The selected state has been updated successfully')
    }).fail(function() {
        showMessage4StatesTab('Error: Could not connect to server', "alert alert-danger");
    });
}

// Delete State
function deleteState() {
    const selectedState = dropDownStateList.find('option:selected');
    const stateId = selectedState.val();

    $.ajax({
        url: deleteStateUrlBase + stateId,
        type: 'DELETE',
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
    }).done(function() {
        // Remove the selected option in the dropdown
        let selectedState = $("#dropDownStateList option:selected");
        selectedState.remove();

        changeFormStateToNew4StatesTab();
        showMessage4StatesTab('The selected state has been deleted successfully')
    }).fail(function() {
        showMessage4StatesTab('Error: Could not connect to server', "alert alert-danger");
    });
}