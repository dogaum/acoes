package br.com.dabage.investments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.dabage.investments.quote.GetQuotation;

@RestController
@RequestMapping("/quote")
@ComponentScan(basePackages = "br.com.dabage.investments.quote")
public class QuoteController {

	@Autowired
	GetQuotation getQuotation;

	@RequestMapping(method = RequestMethod.GET, value = "/{stock}")
	public String checkNews(@PathVariable("stock") String stock) {
		if (stock == null || stock.isEmpty() || stock.length() < 5) {
			return "Invalid Stock";
		}
		return getQuotation.getLastQuotation(stock);
	}
}
