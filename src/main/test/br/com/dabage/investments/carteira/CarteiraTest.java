package br.com.dabage.investments.carteira;

import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.com.dabage.investments.config.ConfigService;
import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.repositories.IncomeRepository;
import br.com.dabage.investments.repositories.NegotiationRepository;
import br.com.dabage.investments.repositories.PortfolioItemRepository;

@ContextConfiguration
(
  {
   "file:src/main/webapp/WEB-INF/mongo-config.xml",
   "file:src/main/webapp/WEB-INF/spring-config.xml",
   "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml"
  }
)
@RunWith(SpringJUnit4ClassRunner.class)
public class CarteiraTest {

	@Autowired
	NegotiationRepository negotiationRepository;

	@Autowired
	CarteiraRepository carteiraRepository;

	@Autowired
	PortfolioService portfolioService;

	@Autowired
	IncomeRepository incomeRepository;

	@Autowired
	PortfolioItemRepository portfolioItemRepository;

	@Autowired
	ConfigService configService;
	
	@Test
	public void testFindByIdCarteiraAndDtNegotiationBetween() {
		CarteiraTO carteira = carteiraRepository.findByName("Ações");
		LinkedList<NegotiationTO> negotiations = negotiationRepository.findByIdCarteiraAndStockOrderByDtNegotiationAsc(carteira.getId(),"CRUZ3");
		for (NegotiationTO negotiationTO : negotiations) {
			PortfolioItemTO item = portfolioItemRepository.findByIdCarteiraAndStock(negotiationTO.getIdCarteira(), negotiationTO.getStock());
			if (item == null) {
				item = new PortfolioItemTO(negotiationTO.getIdCarteira(), negotiationTO.getStock());
			}
			item.addNegotiation(negotiationTO);
			portfolioItemRepository.save(item);
		}
	}

	@Test
	@Rollback(false)
	public void calcPortfolios() {
		configService.calcPortfolioItem();
	}
}
