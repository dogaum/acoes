package br.com.dabage.investments.home;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import br.com.dabage.investments.carteira.CarteiraItemTO;
import br.com.dabage.investments.carteira.IncomeTO;
import br.com.dabage.investments.carteira.NegotiationTO;

public class CarteiraVO implements Serializable {

	/** */
	private static final long serialVersionUID = 478299199272186571L;

	private String name;

	private Double totalPortfolio;

	private Double totalPortfolioActual;

	private Double totalPortfolioPercent;
	
	private Double totalPortfolioIncome;

	private Double totalCalculateResult;

	private Double totalPortfolioActualPlusIncome;

	private Double percentTotalActual;

	private Double percentTotalPos;
	
	private NegotiationTO lastNegotiation;
	
	private IncomeTO lastIncome;
	
	private List<CarteiraItemTO> itens;

	private Map<String, List<IncomeVO>> incomes;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getTotalPortfolio() {
		return totalPortfolio;
	}

	public void setTotalPortfolio(Double totalPortfolio) {
		this.totalPortfolio = totalPortfolio;
	}

	public Double getTotalPortfolioActual() {
		return totalPortfolioActual;
	}

	public void setTotalPortfolioActual(Double totalPortfolioActual) {
		this.totalPortfolioActual = totalPortfolioActual;
	}

	public Double getTotalPortfolioIncome() {
		return totalPortfolioIncome;
	}

	public void setTotalPortfolioIncome(Double totalPortfolioIncome) {
		this.totalPortfolioIncome = totalPortfolioIncome;
	}

	public Double getTotalCalculateResult() {
		return totalCalculateResult;
	}

	public void setTotalCalculateResult(Double totalCalculateResult) {
		this.totalCalculateResult = totalCalculateResult;
	}

	public Double getTotalPortfolioActualPlusIncome() {
		return totalPortfolioActualPlusIncome;
	}

	public void setTotalPortfolioActualPlusIncome(
			Double totalPortfolioActualPlusIncome) {
		this.totalPortfolioActualPlusIncome = totalPortfolioActualPlusIncome;
	}

	public Double getPercentTotalActual() {
		return percentTotalActual;
	}

	public void setPercentTotalActual(Double percentTotalActual) {
		this.percentTotalActual = percentTotalActual;
	}

	public Double getPercentTotalPos() {
		return percentTotalPos;
	}

	public void setPercentTotalPos(Double percentTotalPos) {
		this.percentTotalPos = percentTotalPos;
	}

	public NegotiationTO getLastNegotiation() {
		return lastNegotiation;
	}

	public void setLastNegotiation(NegotiationTO lastNegotiation) {
		this.lastNegotiation = lastNegotiation;
	}

	public IncomeTO getLastIncome() {
		return lastIncome;
	}

	public void setLastIncome(IncomeTO lastIncome) {
		this.lastIncome = lastIncome;
	}

	public List<CarteiraItemTO> getItens() {
		return itens;
	}

	public void setItens(List<CarteiraItemTO> itens) {
		this.itens = itens;
	}

	public Map<String, List<IncomeVO>> getIncomes() {
		return incomes;
	}

	public void setIncomes(Map<String, List<IncomeVO>> incomes) {
		this.incomes = incomes;
	}

	public Double getTotalPortfolioPercent() {
		return totalPortfolioPercent;
	}

	public void setTotalPortfolioPercent(Double totalPortfolioPercent) {
		this.totalPortfolioPercent = totalPortfolioPercent;
	}

}
