function sleep(milliseconds) {
	const wakeUpDate = Date.now() + milliseconds;
	do {/*nothing*/} while (Date.now() < wakeUpDate);
}
const displayFailedRequest = (xhr, status, error) => {
	console.log("KO");
	console.log("Error: " + error);
	console.log("Status: " + status);
	console.dir(xhr);
	alert("Requête échouée, cf. logs");
};

export { sleep, displayFailedRequest }
