package br.com.dabage.investments.carteira;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.dabage.investments.company.IncomeCompanyTO;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.repositories.IncomeCompanyRepository;
import br.com.dabage.investments.repositories.IncomeRepository;
import br.com.dabage.investments.repositories.NegotiationRepository;

@Service
public class PortfolioService {

	@Autowired
	CarteiraRepository carteiraRepository;

	@Autowired
	NegotiationRepository negotiationRepository;

	@Autowired
	IncomeCompanyRepository incomeCompanyRepository;

	@Autowired
	IncomeRepository incomeRepository;

	@Autowired
	GetQuotation getQuotation;

	/**
	 * Rerturn all sell negotiations from a portfolio
	 * @param portfolio
	 * @return
	 */
	public List<NegotiationTO> getSellNegotiations(CarteiraTO portfolio) {
		List<NegotiationTO> result = new ArrayList<NegotiationTO>();
		if (portfolio.getNegotiations() != null) {
			for (NegotiationTO negotiationTO : portfolio.getNegotiations()) {
				if (negotiationTO.getNegotiationType().equals(NegotiationType.Venda)) {
					result.add(negotiationTO);
				}
			}
		}

		return result;
	}

	/**
	 * Return a Global Negotiations from All Portfolios
	 * @param portfolios
	 * @return
	 */
	public List<NegotiationTO> getGlobalSellNegotiations(List<CarteiraTO> portfolios) {
		List<NegotiationTO> result = new ArrayList<NegotiationTO>();
		for (CarteiraTO portfolio : portfolios) {
			result.addAll(this.getSellNegotiations(portfolio));	
		}

		return result;
	}

	/**
	 * Rerturn all sell negotiations from a portfolio
	 * @param portfolio
	 * @return
	 */
	public List<NegotiationTO> getSellNegotiationsByFilter(List<CarteiraTO> portfolios, Date from, Date to) {
		List<NegotiationTO> result = new ArrayList<NegotiationTO>();

		for (CarteiraTO portfolio : portfolios) {
			List<NegotiationTO> all = null;
			if (from != null && to != null) {
				Calendar calFrom = Calendar.getInstance();
				calFrom.setTime(from);
				calFrom.add(Calendar.DAY_OF_MONTH, -1);
				from = calFrom.getTime();

				Calendar calTo = Calendar.getInstance();
				calTo.setTime(to);
				calTo.add(Calendar.DAY_OF_MONTH, 1);
				to = calTo.getTime();

				all = negotiationRepository.findByIdCarteiraAndDtNegotiationBetween(portfolio.getId(), from, to);
			} else if (from != null && to == null) {
				all = negotiationRepository.findByIdCarteiraAndDtNegotiationGreaterThanEqual(portfolio.getId(), from);
			} else if (from == null && to != null) {
				all = negotiationRepository.findByIdCarteiraAndDtNegotiationLessThanEqual(portfolio.getId(), to);
			} else {
				all = negotiationRepository.findByIdCarteira(portfolio.getId());
			}

			if (all != null) {
				for (NegotiationTO negotiationTO : all) {
					if (negotiationTO.getNegotiationType().equals(NegotiationType.Venda)) {
						result.add(negotiationTO);
					}
				}
			}			
		}

		return result;
	}
	
	/**
	 * Return a Portfolio Result
	 * @param negotiations
	 * @return
	 */
	public Double getTotalPortfolioResult(List<NegotiationTO> negotiations) {
		Double totalPortfolioResult = 0D;

		if (negotiations != null) {
			for (NegotiationTO negotiationTO : negotiations) {
				totalPortfolioResult += negotiationTO.getCalculateValue();
			}
		}

		return totalPortfolioResult;
	}

	public void calculatePortfolio(CarteiraTO carteira) {
		if (carteira.getNegotiations() == null) {
			carteira.setNegotiations(new ArrayList<NegotiationTO>());
		}

		List<CarteiraItemTO> itens = new ArrayList<CarteiraItemTO>();
		for (NegotiationTO neg : carteira.getNegotiations()) {
			if (neg == null || neg.getRemoveDate() != null) {
				continue;
			}
			CarteiraItemTO item = new CarteiraItemTO(neg.getStock());
			if (itens.contains(item)) {
				int idx = itens.indexOf(item);
				item = itens.get(idx);
			} else {
				itens.add(item);
			}
			item.addNegotiation(neg);
		}

		if (carteira.getIncomes() == null) {
			carteira.setIncomes(new ArrayList<IncomeTO>());
		}
		carteira.setTotalPortfolioIncome(0D);
		for (IncomeTO inc : carteira.getIncomes()) {
			CarteiraItemTO item = new CarteiraItemTO(inc.getStock());
			if (itens.contains(item)) {
				int idx = itens.indexOf(item);
				item = itens.get(idx);
			} else {
				itens.add(item);
			}
			item.addIncome(inc);
			carteira.setTotalPortfolioIncome(carteira.getTotalPortfolioIncome() + inc.getValue());
		}

		carteira.setTotalPortfolio(0D);
		carteira.setTotalPortfolioActual(0D);
		carteira.setTotalCalculateResult(0D);
		for (CarteiraItemTO item : itens) {
			carteira.setTotalPortfolio(carteira.getTotalPortfolio() + item.getTotalValue());
			item.setActualValue(getQuotation.getLastQuoteCache(item.getStock()));
			IncomeCompanyTO lastIncomeCompany = incomeCompanyRepository.findTopByStockOrderByIncomeDateDesc(item.getStock());
			item.setLastIncomeCompany(lastIncomeCompany);
			if (lastIncomeCompany != null) {
				Double actualDY = (lastIncomeCompany.getValue() / item.getActualValue());
				item.setActualDY(actualDY);					

				Double buyDY = (lastIncomeCompany.getValue() / item.getAvgValue());
				item.setBuyDY(buyDY);
			}

			carteira.setTotalPortfolioActual(carteira.getTotalPortfolioActual() + item.getTotalActual());
			carteira.setTotalCalculateResult(carteira.getTotalCalculateResult() + item.getTotalCalculateResult());
		}
		carteira.setTotalPortfolioActualPlusIncome(carteira.getTotalPortfolioIncome() + carteira.getTotalCalculateResult());
		carteira.setPercentTotalActual((carteira.getTotalPortfolioActual() / carteira.getTotalPortfolio()) - 1);
		carteira.setPercentTotalPos(((carteira
				.getTotalPortfolioActual() + carteira
				.getTotalPortfolioActualPlusIncome()) / carteira
				.getTotalPortfolio()) - 1);

		// Last Negotitation
		if (!carteira.getNegotiations().isEmpty()) {
			NegotiationTO lastNegotiation = carteira.getNegotiations().get(carteira.getNegotiations().size() - 1);
			carteira.setLastNegotiation(lastNegotiation);				
		}

		// Last Income
		IncomeTO lastIncome = incomeRepository.findTopByOrderByIncomeDateDescAddDateDesc();
		carteira.setLastIncome(lastIncome);

		Collections.sort(itens);
		carteira.setItens(itens);
	}
}
