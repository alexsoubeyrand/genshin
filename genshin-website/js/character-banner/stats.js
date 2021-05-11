import * as Memory from '../libs/memory.js';
import * as Loading from '../libs/loading.js';
import * as Debug from '../libs/debug.js';// TODO Remove
import * as Configuration from './configuration.js';

$(document).ready(() => { // Start ready()

let init = new Promise((res, rej) => {res()});

const stateInit = newState => {
	return newState;
};
let memory;

init = init.then(() => Memory.load("character-banner-stats", stateInit));
init = init.then((mem) => {memory = mem});

const totalSpan = $("#character-banner").find("#stats").find("#total");
const statsTable = $("#character-banner").find("#stats").find("table");
statsTable.clean = () => {
	totalSpan.text("0");
	statsTable.find("tr").each((index, item) => {
		if (index === 0) return; // Keep headers row
		item.remove();
	});
};
statsTable.fill = stats => {
	totalSpan.text(stats.counterRuns);
	
	const rows = [
		["3☆", stats.runs3Stars],
		["4☆", stats.runs4Stars],
		["4☆W", stats.runs4StarsWeapons],
		["4☆C", stats.runs4StarsCharacters],
		["5☆", stats.runs5Stars],
		["5☆P", stats.runs5StarsPermanents],
		["5☆E", stats.runs5StarsExclusives],
	];
	rows.forEach(row => {
		const name = row[0];
		const item = row[1];
		statsTable.append(
			"<tr>"
			+"<td>"+name+"</td>"
			+"<td>"+item.counter+"</td>"
			+"<td>"+item.rate*100+" %</td>"
			+"<td>"+item.averageRunsToObtain+"</td>"
			+"</tr>"
		);
	});
};
init = init.then(() => {
	if (memory.state.stats) {
		statsTable.fill(memory.state.stats);
	}
});

init = init.then(() => Configuration.registerResetCallback(() => {
	Loading.screen.show();
	statsTable.clean();
	$.get(Configuration.getCurrentStatsUri())
	.fail(Debug.displayFailedRequest)
	.done(json => {
		memory.state.stats = json;
		memory.save();
		statsTable.fill(memory.state.stats)
		Loading.screen.hide();
	});
}));

}); // End ready()
