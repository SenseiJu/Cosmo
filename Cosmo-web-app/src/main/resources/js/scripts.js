const url = "http://cosmo.senseiju.me:8080/api"

function sendDeleteModelFromPackRequest(model_data, model_type) {
    const request = new XMLHttpRequest()
    request.open("DELETE", url + "?model_data=" + model_data + "?model_type=" + model_type)
    request.send()

    window.location.href = "http://cosmo.senseiju.me:8080/away"
}