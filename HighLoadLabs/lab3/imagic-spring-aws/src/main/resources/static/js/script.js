document.addEventListener("DOMContentLoaded", function() {
    const encryptForm = document.getElementById("encryptForm");
    const decryptForm = document.getElementById("decryptForm");

    encryptForm.addEventListener("mouseover", function() {
        this.style.transform = "translateY(-5px)"; // Simple hover effect in JS
    });

    encryptForm.addEventListener("mouseout", function() {
        this.style.transform = "translateY(0)";
    });

    decryptForm.addEventListener("mouseover", function() {
        this.style.transform = "translateY(-5px)";
    });

    decryptForm.addEventListener("mouseout", function() {
        this.style.transform = "translateY(0)";
    });
});
