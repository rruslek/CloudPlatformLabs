const chooseCity = (element, array) => {
    document.querySelector("#city").innerHTML = element.innerHTML;
    for (let i = 0;i < array.length;i++) {
        if (array[i].innerHTML == element.innerHTML) array[i].classList.add("active");
        else array[i].classList.remove("active");
    }
    document.cookie = "city=" + element.innerHTML;
    $(document).ready(function(){$("#chooseCityModal").modal('hide');});
}  

function getCookie(name) {
    let matches = document.cookie.match(new RegExp(
      "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : undefined;
}

$(document).ready(function(){
    const city = getCookie("city");
    if (city == undefined) $("#chooseCityModal").modal('show');
  });

const city = getCookie("city");

const cities = document.getElementsByClassName("city");

for (let i=0;i<cities.length;i++){
    cities[i].onclick = function(){chooseCity(cities[i], cities)};
    if (cities[i].innerHTML == city) chooseCity(cities[i], cities);
}