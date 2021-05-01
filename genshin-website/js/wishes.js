$(document).ready(() => { // Start ready()

function sleep(milliseconds) {
	const wakeUpDate = Date.now() + milliseconds;
	do {/*nothing*/} while (Date.now() < wakeUpDate);
}

// TODO Not effective, must rethink that stuff with asynchronous calls
let loadingScreen = $("#loadingScreen");
loadingScreen.show = () => {
	loadingScreen.css("display", "block");
}
loadingScreen.hide = () => {
	loadingScreen.css("display", "none");
}

function loadMemory(memoryKey, init) {
	let savedState = localStorage.getItem(memoryKey);
	let state;
	let finalize;
	if (!savedState) {
		state = {};
		finalize = state => init(state);
	} else {
		state = JSON.parse(savedState);
		finalize = state => {};
	}
	let memory = {
		state: state,
	};
	memory.save = () => {
		localStorage.setItem(memoryKey, JSON.stringify(memory.state));
		console.log("Saved: ", memory.state);
	}
	memory.clear = () => {
		memory.state = {};
		return init(memory.state);
	}
	finalize(memory.state);
	return memory;
}

const requestFailureAction = ( xhr, status, error ) => {
	console.log("KO");
	console.log("Error: " + error);
	console.log("Status: " + status);
	console.dir(xhr);
	alert("Requête échouée");
};

let setNextUris = (memoryState, json) => {
	memoryState.nextSingleUri = json._links["http://localhost:8080/rels/next-run"].href;
	memoryState.nextMultiUri = json._links["http://localhost:8080/rels/next-multi"].href;
};
let memoryInit = newState => {
	loadingScreen.show();
	newState.defaultConfUri = "http://localhost:8080/banners/character/configuration";
	return $.get(newState.defaultConfUri)
	.fail(requestFailureAction)
	.done(json => {
		setNextUris(newState, json);
		newState.wishCounter = 0;
		newState.wishList = [];
		loadingScreen.hide();
	});
};
let memory = loadMemory("character-wishes-memory", memoryInit);
memory.updateNextUris = json => {
	setNextUris(memory.state, json);
}
console.log("Loaded: ", memory.state);

const wishesTable = $("#wishes table");
wishesTable.clean = () => {
	wishesTable.find("tr").each((index, item) => {
		if (index === 0) return; // Keep headers row
		item.remove();
	});
};
wishesTable.appendWish = item => {
	wishesTable.append(
		"<tr>"
		+"<td>"+item.counter+"</td>"
		+"<td>"+item.stars+"</td>"
		+"<td>"+item.type+"</td>"
		+"<td>"+item.isExclusive+"</td>"
		+"</tr>"
	);
};
memory.state.wishList.forEach(wishesTable.appendWish);

const singleButton = $("#single");
singleButton.click(event => {
	loadingScreen.show();
	$.get(memory.state.nextSingleUri)
	.fail(requestFailureAction)
	.done(json => {
		memory.state.wishCounter++
		const item = {
			"counter": memory.state.wishCounter,
			"stars": json.stars,
			"type": json.type,
			"isExclusive": json.isExclusive,
		};
		wishesTable.appendWish(item);
		memory.state.wishList.push(item);
		memory.updateNextUris(json);
		memory.save();
		loadingScreen.hide();
	});
});

const multiButton = $("#multi");
multiButton.click(event => {
	loadingScreen.show();
	$.get(memory.state.nextMultiUri)
	.fail(requestFailureAction)
	.done(json => {
		json._embedded.wishList.forEach(item => {
			memory.state.wishCounter++
			item.counter = 	memory.state.wishCounter;
			wishesTable.appendWish(item);
			memory.state.wishList.push(item);
		});
		memory.updateNextUris(json);
		memory.save();
		loadingScreen.hide();
	});
});

const reduceButton = $("#reduce");
reduceButton.click(event => {
	loadingScreen.show();
	wishesTable.clean();
	const item = {
		"counter": "...",
		"stars": "...",
		"type": "...",
		"isExclusive": "...",
	};
	wishesTable.appendWish(item);
	memory.state.wishList = [item];
	memory.save();
	loadingScreen.hide();
});

const resetButton = $("#reset");
resetButton.click(event => {
	loadingScreen.show();
	wishesTable.clean();
	memory.clear()
	.then(() => {
		memory.save();
		loadingScreen.hide();
	});
});

}); // End ready()
