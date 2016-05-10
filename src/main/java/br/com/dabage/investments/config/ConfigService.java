package br.com.dabage.investments.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.dabage.investments.carteira.CarteiraTO;
import br.com.dabage.investments.carteira.DateObject;
import br.com.dabage.investments.carteira.IncomeTO;
import br.com.dabage.investments.carteira.IncomeTypes;
import br.com.dabage.investments.carteira.NegotiationTO;
import br.com.dabage.investments.carteira.PortfolioItemTO;
import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.repositories.IncomeRepository;
import br.com.dabage.investments.repositories.NegotiationRepository;
import br.com.dabage.investments.repositories.PortfolioItemRepository;

@Service
public class ConfigService {

	@Autowired
	CarteiraRepository carteiraRepository;
	
	@Autowired
	NegotiationRepository negotiationRepository;

	@Autowired
	PortfolioItemRepository portfolioItemRepository;

	@Autowired
	IncomeRepository incomeRepository;

	public void calcPortfolioItem() {
		portfolioItemRepository.deleteAll();
		List<CarteiraTO> carteiras = carteiraRepository.findAll();
		for (CarteiraTO carteira : carteiras) {
			HashMap<String, String> stocks = new HashMap<String, String>();
			List<NegotiationTO> negs = carteira.getNegotiations();
			for (NegotiationTO negotiationTO : negs) {
				stocks.put(negotiationTO.getStock(), "");	
			}

			// Calcs all Stocks
			for (String stock : stocks.keySet()) {
				List<DateObject> objects = new ArrayList<DateObject>();
				LinkedList<NegotiationTO> negotiations = negotiationRepository.findByIdCarteiraAndStockOrderByDtNegotiationAsc(carteira.getId(), stock);
				for (NegotiationTO negotiationTO : negotiations) {
					DateObject obj = new DateObject(negotiationTO.getDtNegotiation(), negotiationTO);
					objects.add(obj);
				}

				List<IncomeTO> amortizations = incomeRepository.findByIdCarteiraAndTypeAndStock(carteira.getId(), IncomeTypes.AMORTIZATION, stock);
				for (IncomeTO incomeTO : amortizations) {
					DateObject obj = new DateObject(incomeTO.getIncomeDate(), incomeTO);
					objects.add(obj);
				}

				Collections.sort(objects);

				for (DateObject obj : objects) {
					if (obj.getData() instanceof NegotiationTO) {
						NegotiationTO negotiationTO = (NegotiationTO) obj.getData();
						PortfolioItemTO item = portfolioItemRepository.findByIdCarteiraAndStock(negotiationTO.getIdCarteira(), negotiationTO.getStock());
						if (item == null) {
							item = new PortfolioItemTO(negotiationTO.getIdCarteira(), negotiationTO.getStock());
						}
						item.addNegotiation(negotiationTO);
						portfolioItemRepository.save(item);
					} else if (obj.getData() instanceof IncomeTO) {
						IncomeTO incomeTO = (IncomeTO) obj.getData();
						PortfolioItemTO item = portfolioItemRepository.findByIdCarteiraAndStock(incomeTO.getIdCarteira(), incomeTO.getStock());
						if (item == null) {
							item = new PortfolioItemTO(incomeTO.getIdCarteira(), incomeTO.getStock());
						}
						item.addAmortization(incomeTO);
						portfolioItemRepository.save(item);
					}
				}
			}
		}
	}
}
