let loadingScreen = $("#loadingScreen");
loadingScreen.show = () => {
	loadingScreen.css("display", "block");
}
loadingScreen.hide = () => {
	loadingScreen.css("display", "none");
}

export { loadingScreen as screen }
