package br.com.dabage.investments.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.dabage.investments.repositories.UserRepository;
import br.com.dabage.investments.user.UserTO;


/**
 *
 * @author Douglas Araujo
 */
@Controller
public class MainController {
 
	@Autowired
	private UserRepository userRepository;
	
	
/*	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String defaultPage(ModelMap map) {
		return "redirect:/index";
	}*/
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(ModelMap model) {
		return "logout";
	}
	
	@RequestMapping(value = "/accessdenied")
	public String loginerror(ModelMap model) {
		model.addAttribute("error", "true");
		return "denied";
	}
	
	@RequestMapping(value = "/menu", method = RequestMethod.GET)
	public String menu(ModelMap map) {
		
		return "menu";
	}

	@RequestMapping(value = "/graph", method = RequestMethod.GET)
	public String graph(ModelMap map) {
		
		return "graph";
	}

	@RequestMapping(value = "/fr", method = RequestMethod.GET)
	public String fr(ModelMap map) {
		
		return "fr";
	}

	@RequestMapping(value = "/listUsers", method = RequestMethod.GET)
	public String listUsers(ModelMap map) {
		
		Iterable<UserTO> users= userRepository.findAll();
		map.addAttribute("users", users);
		
		return "listUsers";
	}
	
}

