import * as Url from '../libs/url.js';
import * as Memory from '../libs/memory.js';
import * as Loading from '../libs/loading.js';
import * as Debug from '../libs/debug.js';// TODO Remove
import * as Configuration from './configuration.js';

$(document).ready(() => { // Start ready()

let init = new Promise((res, rej) => {res()});

const setNextUris = (memoryState, json) => {
	memoryState.nextSingleUri = Url.relativeServiceUri(json._links[Url.absoluteServiceUri("/rels/next-run")].href);
	memoryState.nextMultiUri = Url.relativeServiceUri(json._links[Url.absoluteServiceUri("/rels/next-multi")].href);
};
const stateInit = newState => {
	Loading.screen.show();
	return $.get(Configuration.getCurrentConfUri())
	.fail(Debug.displayFailedRequest)
	.done(json => {
		setNextUris(newState, json);
		newState.wishCounter = 0;
		newState.wishList = [];
		Loading.screen.hide();
	})
	.then(() => newState);
};
let memory;

init = init.then(() => Memory.load("character-banner-simulator", stateInit));
init = init.then((mem) => {memory = mem});
init = init.then(() => memory.updateNextUris = json => setNextUris(memory.state, json));
// init.then(() => memory.clear()).then(() => memory.save());

const simulator = $("#character-banner").find("#simulator");

const wishesTable = simulator.find("table");
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

const singleButton = simulator.find("#single");
singleButton.click(event => {
	Loading.screen.show();
	$.get(Url.absoluteServiceUri(memory.state.nextSingleUri))
	.fail(Debug.displayFailedRequest)
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
		Loading.screen.hide();
	});
});

const multiButton = simulator.find("#multi");
multiButton.click(event => {
	Loading.screen.show();
	$.get(Url.absoluteServiceUri(memory.state.nextMultiUri))
	.fail(Debug.displayFailedRequest)
	.done(json => {
		json._embedded.wishList.forEach(item => {
			memory.state.wishCounter++
			item.counter = 	memory.state.wishCounter;
			wishesTable.appendWish(item);
			memory.state.wishList.push(item);
		});
		memory.updateNextUris(json);
		memory.save();
		Loading.screen.hide();
	});
});

const reduceButton = simulator.find("#reduce");
reduceButton.click(event => {
	Loading.screen.show();
	wishesTable.clean();
	const item = {
		isReduced: true,
		counter: "1..." + memory.state.wishCounter,
	};
	wishesTable.appendWish(item);
	memory.state.wishList = [item];
	memory.save();
	Loading.screen.hide();
});

const resetButton = simulator.find("#reset");
resetButton.click(event => {
	Loading.screen.show();
	wishesTable.clean();
	memory.clear()
	.then(() => {
		memory.save();
		Loading.screen.hide();
	});
});
init = init.then(() => Configuration.registerResetCallback(() => resetButton.click()));

}); // End ready()
