import $ from 'jquery';
import { extractDataFromDataUri } from './utils';
import 'bootstrap/dist/css/bootstrap.css';

let $trigger, $messageBox;

function resetButton() {
    $trigger.removeClass('disabled').text('Capture!');
}
function resetMessageBox() {
    $messageBox.removeClass('alert-success').removeClass('alert-danger').text('');
}

function showSuccessMessage(text) {
    $messageBox.text(text).addClass('alert-success');
    resetButton();
}

function showErrorMessage(text) {
    $messageBox.text(text).addClass('alert-danger');
    resetButton();
}

function performCaptureAndRecognizePerson() {
    $trigger.addClass('disabled').text('Please wait...');
    resetMessageBox();

    window.Webcam.snap(imageDataUri => $.ajax({
            type: 'POST',
            url: '/recognize',
            data: JSON.stringify({
                data: extractDataFromDataUri(imageDataUri)
            }),
            success: (response) => {
                if (response.success) {
                    showSuccessMessage('You are: ' + response.prediction);
                } else {
                    showErrorMessage('Recognition failed, please try again');
                }
            },

            error: () => showErrorMessage('Server error'),
            contentType: 'application/json',
            dataType: 'json'
        })
    );
}

$(() => {
    $trigger = $('#trigger');
    $messageBox = $('#result-target');

    window.Webcam.attach('#camera');

    $trigger.click(performCaptureAndRecognizePerson);
});
