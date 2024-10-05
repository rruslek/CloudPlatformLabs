function plusItem(id){
    const counter = document.getElementById(id);
    addToCart(id);
}
function minusItem(id){
    const counter = document.getElementById(id);
    deleteFromCart(id);
}

$(document).ready(function(){
    $('.count').prop('disabled', true);
});