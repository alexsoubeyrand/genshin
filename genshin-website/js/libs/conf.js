// Default values
let servicePath = "http://localhost:8080";

// Updated values from config file
$.ajax({
	type: 'GET',
	url: './conf.json',
	dataType: 'json',
	error: (xhr, status, error) => {
		console.log("Failed to retrieve conf.json, using default values");
		console.log("Status: " + status);
		console.log("Error: " + error);
		console.dir(xhr);
	},
	success: json => {
		if (json.servicePath) {
			servicePath = json.servicePath;
			console.log("servicePath:", servicePath);
		}
	}
});

export {servicePath}
