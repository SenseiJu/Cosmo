const modal = document.getElementById("new-pack-modal")
const openModalButton = document.getElementById("new-pack-modal-button")

openModalButton.onclick = function () {
    modal.style.display = "block"
}

window.onclick = function(event) {
    if (event.target === modal) {
        modal.style.display = "none"
    }
}