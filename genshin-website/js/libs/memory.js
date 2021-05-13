function load(memoryKey, stateInit) {
	// Register a full clean for WIP cleaning buttons
	let process = new Promise((res, rej) => {res()})
	process = process.then(() => {
		$('#wip-clean').click(event => {
			localStorage.removeItem(memoryKey);
			location.reload();
		})
		$('#wip-memory-log').click(event => {
			console.log(memoryKey, JSON.parse(localStorage.getItem(memoryKey)));
		})
	});
	
	// Retrieve state or build it
	process = process.then(() => {
		let savedState = localStorage.getItem(memoryKey);
		if (!savedState) {
			console.log("Init", memoryKey);
			return stateInit({});
		} else {
			console.log("Load", memoryKey);
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
			console.log("Save", memoryKey);
			localStorage.setItem(memoryKey, JSON.stringify(memory.state));
		}
		memory.clear = () => {
			console.log("Clear", memoryKey);
			memory.state = {};
			return stateInit(memory.state);
		}
		return memory
	});
	
	return process;
}

export { load }
