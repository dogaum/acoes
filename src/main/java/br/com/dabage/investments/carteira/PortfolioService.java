package br.com.dabage.investments.carteira;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.repositories.NegotiationRepository;

@Service
public class PortfolioService {

	@Autowired
	CarteiraRepository carteiraRepository;

	@Autowired
	NegotiationRepository negotiationRepository;

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

}
