window.addEventListener('load', function () {
    setTimeout(function () {
        document.body.classList.remove("no-transition-on-load")
    }, 1)
})

function sendDeleteModelFromPackRequest(packId, modelData, modelType) {
    const request = new XMLHttpRequest()
    request.onreadystatechange = function () {
        if (this.readyState === 4) {
            window.location.href = "/packs/" + packId
        }
    }
    request.open(
        "DELETE",
        "/api/packs/models" + "?pack_id=" + packId + "&model_data=" + modelData + "&model_type=" + modelType
    )
    request.send()
}

function sendDeletePackRequest(packId) {
    const request = new XMLHttpRequest()
    request.onreadystatechange = function () {
        if (this.readyState === 4) {
            window.location.href = "/packs"
        }
    }
    request.open(
        "DELETE",
        "/api/packs" + "?pack_id=" + packId
    )
    request.send()
}

function sendSubscribeModelToPackRequest(packId, modelData, modelType) {
    const request = new XMLHttpRequest()
    request.open(
        "POST",
        "/api/packs/models" + "?pack_id=" + packId + "&model_data=" + modelData + "&model_type=" + modelType
    )
    request.send()
}

function sendCreateNewPackRequest() {
    const request = new XMLHttpRequest()
    request.onreadystatechange = function () {
        if (this.readyState === 4) {
            window.location.href = "/packs"
        }
    }
    request.open(
        "POST",
        "/api/packs"
    )
    request.send()
}