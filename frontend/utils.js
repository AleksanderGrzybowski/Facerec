export function extractDataFromDataUri(dataUri) {
    return dataUri.substring(23);
}

export function createDataUri(data) {
    return "data:image/jpeg;base64," + data;
}
