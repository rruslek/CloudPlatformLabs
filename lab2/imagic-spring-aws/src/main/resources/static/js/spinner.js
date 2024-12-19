document.addEventListener("DOMContentLoaded", function() {
    document.body.style.opacity = 0;
    setTimeout(function() {
        document.body.style.transition = "opacity 1s ease-in";
        document.body.style.opacity = 1;
    }, 100);
});

function showSpinner() {
    document.getElementById('spinner').style.display = 'block';
}

function hideSpinner() {
    document.getElementById('spinner').style.display = 'none';
}