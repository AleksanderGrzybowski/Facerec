import $ from 'jquery';
import { createDataUri, extractDataFromDataUri } from './utils';
import 'bootstrap/dist/css/bootstrap.css';

$(() => {
    window.Webcam.attach('#camera');

    const $trigger = $('#trigger');

    $trigger.on('click', () =>
        window.Webcam.snap(dataUri =>
            $.ajax({
                type: 'POST',
                url: '/extractFace',
                data: JSON.stringify({
                    data: extractDataFromDataUri(dataUri)
                }),
                success: successHandler,
                error: () => alert('Server error'),
                contentType: 'application/json',
                dataType: 'json'
            })
        )
    );
});

function successHandler(response) {
    if (response.success) {
        $('#photos').append($('<img/>').attr('src', createDataUri(response.data)));
    } else {
        alert("Face not detected");
    }
}