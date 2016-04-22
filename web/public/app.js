Webcam.attach('#camera');

$('#trigger').click(function triggered() {
    Webcam.snap(function (dataUri) {
        var data = extractData(dataUri);

        $.ajax({
            type: 'POST',
            url: '/recognize',
            data: JSON.stringify({data: data}),
            success: function (response) {
                console.log(response)
            },
            contentType: 'application/json',
            dataType: 'json'
        });
    });
});

function extractData(dataUri) {
    return dataUri.substring(23);
}
