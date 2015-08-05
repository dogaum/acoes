package br.com.dabage.investments.controller;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.dabage.investments.carteira.CarteiraTO;
import br.com.dabage.investments.carteira.NegotiationTO;
import br.com.dabage.investments.carteira.PortfolioService;
import br.com.dabage.investments.carteira.ResultTO;
import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.repositories.NegotiationRepository;
import br.com.dabage.investments.user.UserTO;

@Controller(value="resultView")
@RequestScoped
public class ResultView extends BasicView implements Serializable {

	/** */
	private static final long serialVersionUID = 6601802404818265880L;

	private List<CarteiraTO> portfolios;

	private CarteiraTO selectedPortfolio;

	private List<NegotiationTO> sellNegotiations;

	private List<ResultTO> results;

	private Double totalPortfolioResult;

	private Date filterInitialDate;

	private Date filterFinalDate;

	private static final DateFormat format = new SimpleDateFormat("yyyyMM");

    @Resource
    CarteiraRepository carteiraRepository;

	@Resource
	NegotiationRepository negotiationRepository;

	@Autowired
	PortfolioService portfolioService;

	/**
	 * Initialize this View
	 * @return
	 */
	public String init() {
		UserTO user = getUserLoggedIn();
		portfolios = carteiraRepository.findByUser(user);
		sellNegotiations = portfolioService.getGlobalSellNegotiations(portfolios);
		Collections.sort(sellNegotiations);
		totalPortfolioResult = portfolioService.getTotalPortfolioResult(sellNegotiations);
		calcResults();
		selectedPortfolio = null;

		return "result";
	}

	/**
	 * Calculate results
	 */
	private void calcResults() {
		results = new ArrayList<ResultTO>();
		for (NegotiationTO neg : sellNegotiations) {
			Integer yearMonth = new Integer(format.format(neg.getDtNegotiation()));
			ResultTO result = new ResultTO(yearMonth, neg.getStockType(), neg.getDayTrade());
			if (results.contains(result)) {
				int index = results.indexOf(result);
				result = results.get(index);
			} else {
				results.add(result);
			}
			result.addNegotiation(neg);
		}
		Collections.sort(results);
		Iterator<ResultTO> ite = results.iterator();

		if (ite.hasNext()) {
			ResultTO first = ite.next();
			first.setResultAmount(first.getResult());
			if (first.getFee() < 10D) {
				first.setFeeAmount(first.getFee());
			} else {
				first.setFeeAmount(0D);
			}

			while (ite.hasNext()) {
				ResultTO thisResult = ite.next();
				if (first.getResultAmount() < 0) {
					thisResult.setResultAmount(thisResult.getResult() + first.getResultAmount());
				} else {
					thisResult.setResultAmount(thisResult.getResult());
				}

				thisResult.setFeeAmount(first.getFeeAmount());
				
				if (thisResult.getTotalFee() < 10D) {
					thisResult.setFeeAmount(thisResult.getFee() + thisResult.getFeeAmount());
				}

				first = thisResult;
			}
		}

	}

	/**
	 * Apresentar detalhes de uma carteira
	 */
	public void selectPortfolio() {
		if (selectedPortfolio == null) {
			addWarnMessage("Selecione uma carteira.");
			return;
		} else {
			selectedPortfolio = carteiraRepository.findOne(selectedPortfolio.getId());
			sellNegotiations = portfolioService.getSellNegotiations(selectedPortfolio);
			Collections.sort(sellNegotiations);
			totalPortfolioResult = portfolioService.getTotalPortfolioResult(sellNegotiations);
			calcResults();
		}
	}

	/**
	 * Reset the filter
	 * @param event
	 */
	public void clearFilter(ActionEvent event) {
		filterInitialDate = null;
		filterFinalDate = null;
	}

	/**
	 * Apply the filter
	 * @param event
	 */
	public void applyFilter(ActionEvent event) {
		List<CarteiraTO> filter = new ArrayList<CarteiraTO>();
		if (selectedPortfolio != null) {
			filter.add(selectedPortfolio);
		} else {
			filter.addAll(portfolios);
		}
		sellNegotiations = portfolioService.getSellNegotiationsByFilter(filter, filterInitialDate, filterFinalDate);
		Collections.sort(sellNegotiations);
		totalPortfolioResult = portfolioService.getTotalPortfolioResult(sellNegotiations);
	}

	public List<CarteiraTO> getPortfolios() {
		return portfolios;
	}

	public void setPortfolios(List<CarteiraTO> portfolios) {
		this.portfolios = portfolios;
	}

	public CarteiraTO getSelectedPortfolio() {
		return selectedPortfolio;
	}

	public void setSelectedPortfolio(CarteiraTO selectedPortfolio) {
		this.selectedPortfolio = selectedPortfolio;
	}

	public List<NegotiationTO> getSellNegotiations() {
		return sellNegotiations;
	}

	public void setSellNegotiations(List<NegotiationTO> sellNegotiations) {
		this.sellNegotiations = sellNegotiations;
	}

	public Double getTotalPortfolioResult() {
		return totalPortfolioResult;
	}

	public void setTotalPortfolioResult(Double totalPortfolioResult) {
		this.totalPortfolioResult = totalPortfolioResult;
	}

	public Date getFilterInitialDate() {
		return filterInitialDate;
	}

	public void setFilterInitialDate(Date filterInitialDate) {
		this.filterInitialDate = filterInitialDate;
	}

	public Date getFilterFinalDate() {
		return filterFinalDate;
	}

	public void setFilterFinalDate(Date filterFinalDate) {
		this.filterFinalDate = filterFinalDate;
	}

	public List<ResultTO> getResults() {
		return results;
	}

	public void setResults(List<ResultTO> results) {
		this.results = results;
	}

}