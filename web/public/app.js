/* Yes, this $ soup should be rewritten in Angular/React. But it works */

Webcam.attach('#camera');

var $trigger = $('#trigger');
var $result = $('#result-target');

$trigger.click(function triggered() {

    $trigger.addClass('disabled').text('Please wait...');
    $result.removeClass('alert-success').removeClass('alert-danger').text('');

    Webcam.snap(function (dataUri) {
        $.ajax({
            type: 'POST',
            url: '/recognize',
            data: JSON.stringify({data: extractData(dataUri)}),
            success: successHandler,
            contentType: 'application/json',
            dataType: 'json'
        });
    });
});

function successHandler(response) {
    if (response.success) {
        $result.text('You are person of id = ' + response.prediction).addClass('alert-success');
    } else {
        $result.text('Recognition failed, please try again').addClass('alert-danger');
    }

    $trigger.removeClass('disabled').text('Capture!');
}

function extractData(dataUri) {
    return dataUri.substring(23);
}
