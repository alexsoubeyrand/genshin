import * as Memory from '../libs/memory.js';
import * as Loading from '../libs/loading.js';
import * as Debug from '../libs/debug.js';// TODO Remove

let getCurrentConfUri = () => "http://localhost:8080/banners/character/configuration";
let getCurrentStatsUri = () => "http://localhost:8080/banners/character/stats";
let resetCallbacks = [];
const registerResetCallback = callback => resetCallbacks.push(callback);

$(document).ready(() => { // Start ready()

let init = new Promise((res, rej) => {res()});

const storeServiceConf = (state, json) => {
	state.currentConfUri = json._links.self.href;
	state.currentStatsUri = json._links["http://localhost:8080/rels/stats"].href;
	
	delete json._links;
	delete json._templates;
	
	const random = json.numberGeneratorDescriptor;
	if (random.hasOwnProperty('seed')) {
		random.type = "random";
	} else if (random.hasOwnProperty('fixedValue')) {
		random.type = "fixed";
	} else if (random.hasOwnProperty('values')) {
		random.type = "list";
	} else {
		throw "Unrecognised type: "+JSON.stringify(random);
	}
	
	state.configuration = json;
};
const stateInit = newState => {
	Loading.screen.show();
	return $.get(getCurrentConfUri())
	.fail(Debug.displayFailedRequest)
	.done(json => {
		storeServiceConf(newState, json);
		Loading.screen.hide();
	})
	.then(() => newState);
};
let memory;

init = init.then(() => Memory.load("character-banner-conf", stateInit));
init = init.then((mem) => {memory = mem});
init = init.then(() => getCurrentConfUri = () => memory.state.currentConfUri);
init = init.then(() => getCurrentStatsUri = () => memory.state.currentStatsUri);
init = init.then(() => memory.storeServiceConf = json => storeServiceConf(memory.state, json));
// init.then(() => memory.clear()).then(() => memory.save());

const form = $("#character-banner").find("#configuration").find("form");

const randomSelector = form.find("#randomType");
randomSelector.save = () => {
	const type = randomSelector.val();
	memory.state.randomType = type;
	memory.save();
};
randomSelector.refresh = () => {
	const type = randomSelector.val();
	form.find("tr[class^='random-']").each((index, item) => {
		if (item.className === "random-"+type) {
			item.style.display = null;
		} else {
			item.style.display = "none";
		}
	});
};
randomSelector.getIgnoreInputs = () => {
	const type = randomSelector.val();
	let inputs = [];
	form.find("tr[class^='random-']").each((index, item) => {
		if (item.className !== "random-"+type) {
			inputs.push($(item).find("input"));
		}
	});
	return inputs;
};
randomSelector.change(event => {
	randomSelector.save();
	randomSelector.refresh();
});
randomSelector.setType = (type) => {
	randomSelector.val(type);
	randomSelector.save();
	randomSelector.refresh();
};

form.updateFromMemory = () => {
	const settings = memory.state.configuration.settings;
	form.find("#probability4Stars").val(settings.probability4Stars);
	form.find("#probability4StarsWeaponCharacter").val(settings.probability4StarsWeaponCharacter);
	form.find("#probability5Stars").val(settings.probability5Stars);
	form.find("#probability5StarsPermanentExclusive").val(settings.probability5StarsPermanentExclusive);
	form.find("#guaranty4Stars").val(settings.guaranty4Stars);
	form.find("#guaranty5Stars").val(settings.guaranty5Stars);
	const state = memory.state.configuration.state;
	form.find("#consecutiveWishesBelow4Stars").val(state.consecutiveWishesBelow4Stars);
	form.find("#consecutiveWishesBelow5Stars").val(state.consecutiveWishesBelow5Stars);
	form.find("#isExclusiveGuaranteedOnNext5Stars").prop("checked", state.isExclusiveGuaranteedOnNext5Stars);
	const random = memory.state.configuration.numberGeneratorDescriptor;
	// TODO Update from type in memory
	if (random.type === "random") {
		form.find("#randomSeed").val(random.seed);
	} else if (random.type === "fixed") {
		form.find("#randomValue").val(random.fixedValue);
	} else if (random.type === "list") {
		form.find("#randomList").val(random.values.join(","));
		form.find("#randomListOffset").val(random.offset);
	} else {
		random.type = "random";
		form.find("#randomSeed").val(0);
	}
	randomSelector.setType(random.type);
	form.find(":input").each((index, item) => {
		const value = $(item).val();
		if (value) {
			if (item.name.startsWith("probability")) {
				$(item).val(value * 100);
			}
		}
	});
};
form.createPatch = () => {
	const patch = {};
	form.find("input[type=text], input[type=number]").each((index, item) => {
		let value = $(item).val();
		if (value) {
			if (item.name.startsWith("probability")) {
				value /= 100;
			}
			patch[item.name] = value;
		}
	});
	form.find("input[type=checkbox]").each((index, item) => {
		patch[item.name] = $(item).prop("checked");
	});
	randomSelector.getIgnoreInputs().forEach((item) => {
		delete patch[item.attr("name")];
	});
	if (patch["randomList"]) {
		patch["randomList"] = JSON.parse("["+patch["randomList"]+"]");
	}
	return patch;
};
init = init.then(() => form.updateFromMemory());

const applyButton = form.find("#apply");
applyButton.click(event => {
	event.preventDefault();
	Loading.screen.show();
	$.ajax({
		type: 'PATCH',
		url: getCurrentConfUri(),
		contentType: 'application/json',
		data: JSON.stringify(form.createPatch()),
		processData: false,
		error: Debug.displayFailedRequest, 
		success: json => {
			memory.storeServiceConf(json);
			memory.save();
			form.updateFromMemory();
			resetCallbacks.forEach(callback => callback());
			Loading.screen.hide();
		}
	});
});
form.find(":input").each((index, input) => {
	const enterKeyCode = 13;
	$(input).keypress(event => {
		if (event.keyCode === enterKeyCode) {
			event.preventDefault();
			applyButton.click();
		}
	});
});


}); // End ready()

export { getCurrentConfUri, getCurrentStatsUri, registerResetCallback }
