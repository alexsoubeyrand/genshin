package fr.sazaju.genshin.service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rels"/* TODO , produces = "application/hal+json" */)
public class RelsController {

	@GetMapping("/source")
	@ResponseBody
	public Object source() {
		throw new NotImplementedException();
	}

	@GetMapping("/default")
	@ResponseBody
	public Object defaultt() {
		throw new NotImplementedException();
	}

	@GetMapping("/mihoyo")
	@ResponseBody
	public Object mihoyo() {
		throw new NotImplementedException();
	}

	@GetMapping("/packs")
	@ResponseBody
	public Object packs() {
		throw new NotImplementedException();
	}

	@GetMapping("/first-order")
	@ResponseBody
	public Object firstOrder() {
		throw new NotImplementedException();
	}

	@GetMapping("/next-orders")
	@ResponseBody
	public Object nextOrders() {
		throw new NotImplementedException();
	}

	@GetMapping("/character-banner")
	@ResponseBody
	public Object characterBanner() {
		throw new NotImplementedException();
	}

	@GetMapping("/next-run")
	@ResponseBody
	public Object nextRun() {
		throw new NotImplementedException();
	}

	@GetMapping("/next-multi")
	@ResponseBody
	public Object nextMulti() {
		throw new NotImplementedException();
	}

	@GetMapping("/wish")
	@ResponseBody
	public Object wish() {
		throw new NotImplementedException();
	}

	@GetMapping("/wishes")
	@ResponseBody
	public Object wishes() {
		throw new NotImplementedException();
	}

	@GetMapping("/settings")
	@ResponseBody
	public Object settings() {
		throw new NotImplementedException();
	}

	@GetMapping("/profile")
	@ResponseBody
	public Object profile() {
		throw new NotImplementedException();
	}

	@GetMapping("/profile-start")
	@ResponseBody
	public Object profileStart() {
		throw new NotImplementedException();
	}

	@GetMapping("/profile-end")
	@ResponseBody
	public Object profileEnd() {
		throw new NotImplementedException();
	}

	@GetMapping("/configuration")
	@ResponseBody
	public Object configuration() {
		throw new NotImplementedException();
	}

	@GetMapping("/next-configuration")
	@ResponseBody
	public Object nextConfiguration() {
		throw new NotImplementedException();
	}
}
