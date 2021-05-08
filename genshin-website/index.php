<!DOCTYPE html>
<html lang="fr">
	<head>
		<meta charset="utf-8">
		<title>Outils Genshin Impact</title>
		<script src="js/libs/jquery-3.6.0.min.js"></script><!-- TODO Import as module where needed -->
		<script type="module" src="js/main.js"></script>
		<link rel="stylesheet" href="css/style.css">
	</head>
	<body>
		<h1>Outils Genshin Impact</h1>
		<nav id="tab-list">
			<button onclick="openTab('home')">Accueil</button>
			<button onclick="openTab('character-banner')">Simulateur de tirages</button>
			<button onclick="openTab('contact')">Contact</button>
			<script type="text/javascript">
				function openTab(idToDisplay) {
					$("#tabs").children().each((index, tab) => tab.style.display = (tab.id === idToDisplay ? "block" : "none"));
					const isClickedButton = (button) => $(button).attr('onclick').includes("'"+idToDisplay+"'");
					$("#tab-list").children("button").each((index, button) => button.className = (isClickedButton(button) ? "selected" : undefined));
					localStorage.currentTab = idToDisplay;
				}
				// Show home by default
				$(document).ready(() => openTab(localStorage.currentTab ?? "home"));
			</script>
		</nav>
		<div id="tabs">
			<div id="home"><?php include "./home.html"; ?></div>
			<div id="contact"><?php include "./contact.html"; ?></div>
			<div id="character-banner"><?php include "./character-banner.html"; ?></div>
		</div>
		<div id="loadingScreen">
			<div id="loadingContent">
				<p>Loading..</p>
			</div>
		</div>
	</body>
</html>
