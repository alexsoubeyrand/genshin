$(document).ready(() => { // Start ready()

let init = new Promise((res, rej) => {res()});

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

function loadMemory(memoryKey, stateInit) {
	// Register a full clean for WIP cleaning buttons
	let process = new Promise((res, rej) => {res()})
	process = process.then(() => {
		$('.wip-clean').click(event => {
			localStorage.removeItem(memoryKey);
			location.reload();
		})
	});
	
	// Retrieve state or build it
	process = process.then(() => {
		let savedState = localStorage.getItem(memoryKey);
		if (!savedState) {
			console.log("Init");
			return stateInit({});
		} else {
			return JSON.parse(savedState);
		}
	});
	
	// Create & enrich memory
	let memory;
	process = process.then(state => {
		memory = {
			state: state,
		};
		memory.save = () => {
			localStorage.setItem(memoryKey, JSON.stringify(memory.state));
			console.log("Saved: ", memory.state);
		}
		memory.clear = () => {
			console.log("Clear");
			memory.state = {};
			return stateInit(memory.state);
		}
		return memory
	});
	
	return process;
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
let stateInit = newState => {
	loadingScreen.show();
	newState.defaultConfUri = "http://localhost:8080/banners/character/configuration";
	return $.get(newState.defaultConfUri)
	.fail(requestFailureAction)
	.done(json => {
		setNextUris(newState, json);
		newState.wishCounter = 0;
		newState.wishList = [];
		loadingScreen.hide();
	})
	.then(() => newState);
};
let memory;

init = init.then(() => loadMemory("character-wishes-memory", stateInit));
init = init.then((mem) => {memory = mem});
init = init.then(() => {memory.updateNextUris = json => setNextUris(memory.state, json)});
init = init.then(() => console.log("Loaded: ", memory.state));
// init.then(() => memory.clear()).then(() => memory.save());

const wishesTable = $("#wishes table");
wishesTable.clean = () => {
	wishesTable.find("tr").each((index, item) => {
		if (index === 0) return; // Keep headers row
		item.remove();
	});
};
wishesTable.appendWish = item => {
	let tagClasses;
	let wishDescription;
	if (item.isReduced) {
		tagClasses = "reduced";
		wishDescription = "...";
	} else {
		tagClasses = [];
		tagClasses.push((item.type+item.stars+"stars").toLowerCase());
		if (item.isExclusive) {
			tagClasses.push("exclusive");
		}
		tagClasses = tagClasses.join(" ");
		
		wishDescription = (item.stars + "☆ " + item.type).toLowerCase();
		if (item.isExclusive) {
			wishDescription += " [EX]";
		}
	}
	
	wishesTable.append(
		"<tr class='" + tagClasses + "'>"
		+"<td class='counter'>"+item.counter+"</td>"
		+"<td>"+wishDescription+"</td>"
		+"</tr>"
	);
};
init = init.then(() => memory.state.wishList.forEach(wishesTable.appendWish));

const singleButton = $("#single");
singleButton.click(event => {
	loadingScreen.show();
	$.get(memory.state.nextSingleUri)
	.fail(requestFailureAction)
	.done(json => {
		memory.state.wishCounter++
		const item = {
			counter: memory.state.wishCounter,
			stars: json.stars,
			type: json.type,
			isExclusive: json.isExclusive,
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
		isReduced: true,
		counter: "1..." + memory.state.wishCounter,
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
