package br.com.dabage.investments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.dabage.investments.news.CheckNews;

@RestController
@RequestMapping("/checknews")
@ComponentScan(basePackages = "br.com.dabage.investments.news")
public class CheckNewsController {

	@Autowired
	CheckNews checkNews;
	
	@RequestMapping(method = RequestMethod.GET, value = "/{query}")
	public String checkNews(@PathVariable("query") String query) {
		checkNews.run(query);
		if (query == null || query.isEmpty()) {
			query = "TODOS";
		}
		return "Fatos Relevantes sobre " + query + " obtidos com sucesso";
	}

}
