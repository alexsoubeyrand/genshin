function load(memoryKey, stateInit) {
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

export { load }
