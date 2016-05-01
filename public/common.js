function extractDataFromDataUri(dataUri) {
    return dataUri.substring(23);
}

function createDataUri(data) {
    return "data:image/jpeg;base64," + data;
}
