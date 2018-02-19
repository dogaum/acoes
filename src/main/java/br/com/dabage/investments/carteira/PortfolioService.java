package br.com.dabage.investments.carteira;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import br.com.dabage.investments.company.IncomeCompanyTO;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.IncomeCompanyRepository;
import br.com.dabage.investments.repositories.IncomeRepository;
import br.com.dabage.investments.repositories.NegotiationRepository;
import br.com.dabage.investments.repositories.PortfolioItemRepository;

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

	@Autowired
	PortfolioItemRepository portfolioItemRepository;

	@Autowired
	MongoTemplate template;

	@Autowired
	CompanyRepository companyRepository;

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
			if (neg == null) {
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
			if (inc == null) {
				continue;
			}
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
		carteira.setTotalLastIncome(0D);
		for (CarteiraItemTO item : itens) {
			item.setCompany(companyRepository.findByTicker(item.getStock()));
			PortfolioItemTO portItem = portfolioItemRepository.findByIdCarteiraAndStock(carteira.getId(), item.getStock());
			if (portItem != null) {
				item.setQuantity(portItem.getQuantity());
				item.setAvgQuantity(portItem.getQuantity());
				item.setAvgValue(portItem.getAvgPrice());
				item.setTotalCalculateResult(portItem.getAccumulatedResult());				
			}

			carteira.setTotalPortfolio(carteira.getTotalPortfolio() + item.getTotalValue());
			item.setActualValue(getQuotation.getLastQuoteCache(item.getStock()));
			List<IncomeCompanyTO> lastIncomeCompanies = incomeCompanyRepository.findTopByStockOrderByIncomeDateDesc(item.getStock());
			if (lastIncomeCompanies != null && !lastIncomeCompanies.isEmpty()) {
				IncomeCompanyTO lastIncomeCompany = lastIncomeCompanies.get(0);
				item.setLastIncomeCompany(lastIncomeCompany);
				if (lastIncomeCompany != null) {
					Double actualDY = (lastIncomeCompany.getValue() / item.getActualValue());
					item.setActualDY(actualDY);					

					Double buyDY = 0D;
					if (item.getAvgValue() != null && !item.getAvgValue().equals(0D)) {
						buyDY = (lastIncomeCompany.getValue() / item.getAvgValue());
					}
					item.setBuyDY(buyDY);
				}				
			}

			carteira.setTotalPortfolioActual(carteira.getTotalPortfolioActual() + item.getTotalActual());
			carteira.setTotalCalculateResult(carteira.getTotalCalculateResult() + item.getTotalCalculateResult());
			if (item.getQuantity() != null && item.getLastIncomeCompany() != null) {
				carteira.setTotalLastIncome(carteira.getTotalLastIncome() + (item.getQuantity() * item.getLastIncomeCompany().getValue()));	
			}

		}
		carteira.setTotalPortfolioActualPlusIncome(carteira.getTotalPortfolioIncome() + carteira.getTotalCalculateResult());
		carteira.setPercentTotalActual((carteira.getTotalPortfolioActual() / carteira.getTotalPortfolio()) - 1);
		carteira.setPercentTotalPos(((carteira
				.getTotalPortfolioActual() + carteira
				.getTotalPortfolioActualPlusIncome()) / carteira
				.getTotalPortfolio()) - 1);

		// Total Buy and Actual DY
		if (carteira.getTotalLastIncome() != null) {
			carteira.setTotalBuyDY((carteira.getTotalLastIncome() / carteira.getTotalPortfolio()));
			carteira.setTotalActualDY((carteira.getTotalLastIncome() / carteira.getTotalPortfolioActual()));
		}

		// Last Negotitation
		if (!carteira.getNegotiations().isEmpty()) {
			NegotiationTO lastNegotiation = carteira.getNegotiations().get(carteira.getNegotiations().size() - 1);
			carteira.setLastNegotiation(lastNegotiation);				
		}

		// Last Income
		IncomeTO lastIncome = incomeRepository.findFirstByOrderByIncomeDateDescAddDateDesc().get(0);
		carteira.setLastIncome(lastIncome);

		Collections.sort(itens);
		carteira.setItens(itens);
	}

	public List<StatementVO> findStatementByFilterOld(StatementFilter filter) {
		List<StatementVO> result = new ArrayList<StatementVO>();

		if (filter.getType().equals(StatementType.Negotiation)) {
			List<NegotiationTO> negotiations = null;
			if (filter.getStock() != null && !filter.getStock().isEmpty()) {
				negotiations = negotiationRepository.findByStockOrderByDtNegotiationAsc(filter.getStock());
			} else {
				negotiations = negotiationRepository.findAll(new Sort(Sort.Direction.ASC, "dtNegotiation"));
			}

			for (NegotiationTO negotiationTO : negotiations) {
				StatementVO st = new StatementVO();
				st.setDate(negotiationTO.getDtNegotiation());
				st.setStock(negotiationTO.getStock());
				st.setValue(negotiationTO.getValue());
				st.setQuantity(negotiationTO.getQuantity());
				st.setAmount(negotiationTO.getQuantity() * negotiationTO.getValue());
				st.setStatementType(negotiationTO.getNegotiationType().name());

				result.add(st);
			}			
		} else {
			
			List<IncomeTO> incomes = null;
			
			if (filter.getStock() != null && !filter.getStock().isEmpty()) {
				incomes = incomeRepository.findByStockOrderByIncomeDateAsc(filter.getStock());
			} else {
				incomes = incomeRepository.findAll(new Sort(Sort.Direction.ASC, "incomeDate"));
			}
			
			for (IncomeTO incomeTO : incomes) {
				StatementVO st = new StatementVO();
				st.setDate(incomeTO.getIncomeDate());
				st.setStock(incomeTO.getStock());
				st.setValue(incomeTO.getValue());
				st.setQuantity(1L);
				st.setAmount(incomeTO.getValue());
				st.setStatementType(incomeTO.getType());

				result.add(st);
			}
		}

		return result;
	}

	public List<StatementVO> findStatementByFilter(StatementFilter filter) {
		List<StatementVO> result = new ArrayList<StatementVO>();
		Query query = new Query();
		
		if (filter.getType().equals(StatementType.Negotiation)) {

			if (filter.getStock() != null && !filter.getStock().isEmpty()) {
				query.addCriteria(Criteria.where("stock").is(filter.getStock().toUpperCase()));
			}

			if (filter.getFilterInitialDate() != null) {
				query.addCriteria(Criteria.where("dtNegotiation").gte(filter.getFilterInitialDate()));
			}

			if (filter.getFilterFinalDate() != null) {
				query.addCriteria(Criteria.where("dtNegotiation").lte(filter.getFilterFinalDate()));
			}

			if (!filter.isSort()) {
				query.with(new Sort(Sort.Direction.ASC, "dtNegotiation"));
			} else {
				query.with(new Sort(Sort.Direction.DESC, "dtNegotiation"));
			}

			List<NegotiationTO> negotiations = template.find(query, NegotiationTO.class);

			for (NegotiationTO negotiationTO : negotiations) {
				StatementVO st = new StatementVO();
				st.setDate(negotiationTO.getDtNegotiation());
				st.setStock(negotiationTO.getStock());
				st.setValue(negotiationTO.getValue());
				st.setCosts(negotiationTO.getCosts());
				st.setQuantity(negotiationTO.getQuantity());
				st.setAvgPrice(negotiationTO.getAvgBuyValue());

				Double amount = negotiationTO.getQuantity() * negotiationTO.getValue();
				if (negotiationTO.getNegotiationType().equals(NegotiationType.Compra)) {
					amount += negotiationTO.getCosts();
				} else {
					amount -= negotiationTO.getCosts();
				}
				st.setAmount(amount);
				st.setStatementType(negotiationTO.getNegotiationType().name());

				result.add(st);
			}
		} else {
			if (filter.getStock() != null && !filter.getStock().isEmpty()) {
				query.addCriteria(Criteria.where("stock").is(filter.getStock().toUpperCase()));
			}

			if (filter.getFilterInitialDate() != null) {
				query.addCriteria(Criteria.where("incomeDate").gte(filter.getFilterInitialDate()));
			}

			if (filter.getFilterFinalDate() != null) {
				query.addCriteria(Criteria.where("incomeDate").lte(filter.getFilterFinalDate()));
			}

			if (!filter.isSort()) {
				query.with(new Sort(Sort.Direction.ASC, "incomeDate"));
			} else {
				query.with(new Sort(Sort.Direction.DESC, "incomeDate"));
			}

			List<IncomeTO> incomes = template.find(query, IncomeTO.class);

			for (IncomeTO incomeTO : incomes) {
				StatementVO st = new StatementVO();
				st.setDate(incomeTO.getIncomeDate());
				st.setStock(incomeTO.getStock());
				st.setValue(incomeTO.getValue());
				st.setQuantity(1L);
				st.setAmount(incomeTO.getValue());
				st.setStatementType(incomeTO.getType());

				result.add(st);
			}
		}

		return result;
	}
}
