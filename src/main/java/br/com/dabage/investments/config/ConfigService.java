package br.com.dabage.investments.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.dabage.investments.carteira.CarteiraTO;
import br.com.dabage.investments.carteira.NegotiationTO;
import br.com.dabage.investments.carteira.PortfolioItemTO;
import br.com.dabage.investments.repositories.CarteiraRepository;
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
				LinkedList<NegotiationTO> negotiations = negotiationRepository.findByIdCarteiraAndStockOrderByDtNegotiationAsc(carteira.getId(), stock);
				// TODO Buscar as amortizacoes e converter para DateObject para fazer a ordenacao.
				for (NegotiationTO negotiationTO : negotiations) {
					PortfolioItemTO item = portfolioItemRepository.findByIdCarteiraAndStock(negotiationTO.getIdCarteira(), negotiationTO.getStock());
					if (item == null) {
						item = new PortfolioItemTO(negotiationTO.getIdCarteira(), negotiationTO.getStock());
					}
					item.addNegotiation(negotiationTO);
					portfolioItemRepository.save(item);
				}
			}
		}
	}
}
