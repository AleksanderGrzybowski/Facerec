Webcam.attach('#camera');

var $trigger = $('#trigger');

$trigger.click(function triggered() {

    Webcam.snap(function (dataUri) {
        $.ajax({
            type: 'POST',
            url: '/extractFace',
            data: JSON.stringify({
                data: extractData(dataUri)
            }),
            success: successHandler,
            error: function () {
                alert('Server error');
            },
            contentType: 'application/json',
            dataType: 'json'
        });
    });
});

function successHandler(response) {
    console.log(response);
    var uri = "data:image/jpeg;base64," + response.data;
    var $img = $('<img/>').attr('src', uri);
    $('#photos').append($img);

}

/**
 * Strip the prefix of data url and preserve
 * only base64 data.
 */
function extractData(dataUri) {
    return dataUri.substring(23);
}
