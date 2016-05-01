Webcam.attach('#camera');

var $trigger = $('#trigger');

$trigger.click(function triggered() {

    Webcam.snap(function (dataUri) {
        $.ajax({
            type: 'POST',
            url: '/extractFace',
            data: JSON.stringify({
                data: extractDataFromDataUri(dataUri)
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
    if (!response.success) {
        alert("Face not detected");
        return;
    }
    
    var uri = createDataUri(response.data);
    var $img = $('<img/>').attr('src', uri);
    
    $img.dblclick(function () {
        $(this).remove();
    });
    
    $('#photos').append($img);
}