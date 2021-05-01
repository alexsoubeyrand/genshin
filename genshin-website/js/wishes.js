$(document).ready(() => { // Start ready()

const requestFailureAction = ( xhr, status, error ) => {
	console.log("KO");
	console.log("Error: " + error);
	console.log("Status: " + status);
	console.dir(xhr);
	alert("Requête échouée");
};

const wishesTable = $("#wishesTable");
const firstWishesButton = $("#firstWishes");
const nextWishesButton = $("#nextWishes");
const resetWishesButton = $("#resetWishes");

const store_firstWishesURI = "first-wishes-uri"
const store_nextWishesURI = "next-wishes-uri"
const store_lastWishesURI = "last-wishes-uri"
localStorage.setItem(store_firstWishesURI, "http://localhost:8080/banners/character/wishes");

const updateWishesTable = json => {
	wishesTable.find("tr").each((index, item) => {
		if (index === 0) return; // Keep headers row
		item.remove();
	});
	json._embedded.wishList.forEach(item => {
		wishesTable.append(
			"<tr>"
			+"<td>"+item.stars+"</td>"
			+"<td>"+item.type+"</td>"
			+"<td>"+item.isExclusive+"</td>"
			+"</tr>"
		);
	});
	
	let next = json._links.next;
	if (!next) {
		localStorage.removeItem(store_nextWishesURI);
		nextWishesButton.prop('disabled', true);
	} else {
		localStorage.setItem(store_nextWishesURI, next.href);
		nextWishesButton.prop('disabled', false);
	}
};
const clearWishesTable = () => {
	wishesTable.find("tr").each((index, item) => {
		if (index === 0) return; // Keep headers row
		item.remove();
	});
	localStorage.removeItem(store_nextWishesURI);
	localStorage.removeItem(store_lastWishesURI);
	nextWishesButton.prop('disabled', true);
};

const requestWishes = uri => {
	$.get(uri)
	.fail(requestFailureAction)
	.done(json => {
		localStorage.setItem(store_lastWishesURI, uri);
		updateWishesTable(json);
	});
};
const lastRequest = localStorage.getItem(store_lastWishesURI);
if (lastRequest) {
	requestWishes(lastRequest);
} else {
	clearWishesTable();
}
firstWishesButton.click(event => requestWishes(localStorage.getItem(store_firstWishesURI)));
nextWishesButton.click(event => requestWishes(localStorage.getItem(store_nextWishesURI)));
resetWishesButton.click(event => clearWishesTable());

}); // End ready()
