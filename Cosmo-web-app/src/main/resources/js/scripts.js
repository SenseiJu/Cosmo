const url = "http://cosmo.senseiju.me:8080/api/packs/models"

window.addEventListener('load', function () {
    setTimeout(function () {
        document.body.classList.remove("no-transition-on-load")
    }, 1)
})

function sendDeleteModelFromPackRequest(packId, modelData, modelType) {
    const request = new XMLHttpRequest()
    request.onreadystatechange = function () {
        if (this.readyState === 4) {
            window.location.href = "http://cosmo.senseiju.me:8080/packs/" + packId
        }
    }
    request.open(
        "DELETE",
        url + "?pack_id=" + packId + "&model_data=" + modelData + "&model_type=" + modelType
    )
    request.send()
}