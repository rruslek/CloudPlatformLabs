const chooseFilter = (element) => {
    if (sessionStorage.getItem("filter") == element.id) {
        sessionStorage.setItem("filter", "ALL");
    }
    else {
        sessionStorage.setItem("filter", element.id);
    }
    checkSortFilter();
}


const sortBy = (element) => {
    sessionStorage.setItem("sort", element.id);
    checkSortFilter();
}

const checkSortFilter = () => {
    const sortMethod = sessionStorage.getItem("sort");
    const filterType = sessionStorage.getItem("filter");
    window.location.replace(`?sortDir=${sortMethod}&type=${filterType}`);
}

if (sessionStorage.getItem("sort") == undefined) sessionStorage.setItem("sort","asc");
if (sessionStorage.getItem("filter") == undefined) sessionStorage.setItem("filter","ALL");

if (window.location.href == "/") {
    sessionStorage.setItem("sort","asc");
    sessionStorage.setItem("filter","ALL");
}

const sort = sessionStorage.getItem("sort");
const filter = sessionStorage.getItem("filter");


const casual = document.querySelector("#CASUAL");
const sport = document.querySelector("#RUNNING");
const low = document.querySelector("#LOW");
const high = document.querySelector("#HIGH");

const array = [casual, sport, low, high];

for (let i = 0;i < array.length;i++) {
    if (array[i].id == filter) {
        array[i].classList.add("active");
    }
}

casual.onclick = function(){chooseFilter(casual)};
sport.onclick = function(){chooseFilter(sport)};
low.onclick = function(){chooseFilter(low)};
high.onclick = function(){chooseFilter(high)};


const ascending = document.querySelector("#asc")
const descending = document.querySelector("#desc")

ascending.onclick = function(){sortBy(ascending)};
descending.onclick = function(){sortBy(descending)};

if (sort == "desc") document.querySelector("#sort").innerHTML = descending.innerHTML;
else document.querySelector("#sort").innerHTML = ascending.innerHTML;