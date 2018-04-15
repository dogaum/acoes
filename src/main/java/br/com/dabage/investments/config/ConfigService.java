package br.com.dabage.investments.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.dabage.investments.carteira.CarteiraTO;
import br.com.dabage.investments.carteira.DateObject;
import br.com.dabage.investments.carteira.IncomeTO;
import br.com.dabage.investments.carteira.IncomeTypes;
import br.com.dabage.investments.carteira.NegotiationTO;
import br.com.dabage.investments.carteira.PortfolioItemTO;
import br.com.dabage.investments.company.CompanyTO;
import br.com.dabage.investments.mail.SendMailSSL;
import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.IncomeRepository;
import br.com.dabage.investments.repositories.NegotiationRepository;
import br.com.dabage.investments.repositories.PortfolioItemRepository;
import br.com.dabage.investments.repositories.UserRepository;
import br.com.dabage.investments.user.UserTO;
import br.com.dabage.investments.utils.HtmlParserUtils;

@Service
public class ConfigService {

	private Logger log = Logger.getLogger(ConfigService.class);

	@Autowired
	CarteiraRepository carteiraRepository;
	
	@Autowired
	NegotiationRepository negotiationRepository;

	@Autowired
	PortfolioItemRepository portfolioItemRepository;

	@Autowired
	IncomeRepository incomeRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CompanyRepository companyRepository;

	public void calcPortfolioItem() {
		log.trace("ConfigService.calcPortfolioItem is executing.");
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
						negotiationRepository.save(negotiationTO);
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
		log.trace("ConfigService.calcPortfolioItem has finish.");
	}

	/**
	 * Send email for IR
	 * @param year
	 */
	public void calculatePortfolioIR(Integer year) {
		Calendar calReference = Calendar.getInstance();
		calReference.set(Calendar.YEAR, year);
		calReference.set(Calendar.MONTH,calReference.getActualMaximum(Calendar.MONTH));
		calReference.set(Calendar.DAY_OF_MONTH,calReference.getActualMaximum(Calendar.DAY_OF_MONTH));
		calReference.set(Calendar.HOUR_OF_DAY,calReference.getActualMaximum(Calendar.HOUR_OF_DAY));
		calReference.set(Calendar.MINUTE,calReference.getActualMaximum(Calendar.MINUTE));

		Collection<UserTO> users = userRepository.findAll();
		for (UserTO user : users) {
			StringBuffer email = new StringBuffer();
			email.append("Usuario: " + user.getName() + " " + user.getSurname() + "\n\n");
			// CSV Header
			email.append("Carteira;");
			email.append("Ativo;");
			email.append("Nome;");
			email.append("CNPJ;");
			email.append("Quantidade;");
			email.append("Preco Medio;");
			email.append("Total ativo;");
			email.append("\n");

			List<CarteiraTO> carteiras = carteiraRepository.findByUser(user);
			for (CarteiraTO carteira : carteiras) {

				List<PortfolioItemTO> itens = new ArrayList<PortfolioItemTO>();
				HashMap<String, String> stocks = new HashMap<String, String>();
				List<NegotiationTO> negs = carteira.getNegotiations();
				for (NegotiationTO negotiationTO : negs) {
					stocks.put(negotiationTO.getStock(), "");
				}

				// Calcs all Stocks
				for (String stock : stocks.keySet()) {
					List<DateObject> objects = new ArrayList<DateObject>();
					LinkedList<NegotiationTO> negotiations = negotiationRepository
							.findByIdCarteiraAndStockOrderByDtNegotiationAsc(
									carteira.getId(), stock);
					for (NegotiationTO negotiationTO : negotiations) {
						if (negotiationTO == null || negotiationTO.getDtNegotiation().after(calReference.getTime())) {
							continue;
						}
						DateObject obj = new DateObject(
								negotiationTO.getDtNegotiation(), negotiationTO);
						objects.add(obj);
					}

					List<IncomeTO> amortizations = incomeRepository
							.findByIdCarteiraAndTypeAndStock(carteira.getId(),
									IncomeTypes.AMORTIZATION, stock);
					for (IncomeTO incomeTO : amortizations) {
						if (incomeTO == null || incomeTO.getIncomeDate().after(calReference.getTime())) {
							continue;
						}
						DateObject obj = new DateObject(
								incomeTO.getIncomeDate(), incomeTO);
						objects.add(obj);
					}

					Collections.sort(objects);

					for (DateObject obj : objects) {
						if (obj.getData() instanceof NegotiationTO) {
							NegotiationTO negotiationTO = (NegotiationTO) obj.getData();
							PortfolioItemTO item = new PortfolioItemTO(
									negotiationTO.getIdCarteira(),
									negotiationTO.getStock());
							boolean found = false;
							for (PortfolioItemTO itemTO : itens) {
								if (negotiationTO.getStock().equals(itemTO.getStock())) {
									item = itemTO;
									found = true;
									break;
								}
							}
							item.addNegotiation(negotiationTO);
							if (!found) itens.add(item);
						} else if (obj.getData() instanceof IncomeTO) {
							IncomeTO incomeTO = (IncomeTO) obj.getData();
							PortfolioItemTO item = new PortfolioItemTO(
									incomeTO.getIdCarteira(),
									incomeTO.getStock());
							boolean found = false;
							for (PortfolioItemTO itemTO : itens) {
								if (incomeTO.getStock().equals(itemTO.getStock())) {
									item = itemTO;
									found = true;
									break;
								}
							}
							item.addAmortization(incomeTO);
							if (!found) itens.add(item);
						}
					}

				}
				carteira.setPortfolioItens(itens);
				for (PortfolioItemTO portfolioItem : itens) {
					CompanyTO company = companyRepository.findByTicker(portfolioItem.getStock());
					email.append(carteira.getName() + ";");
					email.append(portfolioItem.getStock() + ";");
					email.append(company.getName() + ";");
					email.append(company.getCnpj() + ";");
					email.append(portfolioItem.getQuantity() + ";");
					email.append(HtmlParserUtils.formatDouble(portfolioItem.getAvgPrice()) + ";");
					email.append(HtmlParserUtils.formatDouble(portfolioItem.getQuantity() * portfolioItem.getAvgPrice()) + "\n");
				}
			}
			// Send email
			SendMailSSL.send("Carteira referente ao ano de " + year, email.toString(), null, user.getEmail());
		}
	}
}
