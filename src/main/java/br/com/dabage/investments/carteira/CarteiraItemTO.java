package br.com.dabage.investments.carteira;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import br.com.dabage.investments.company.IncomeCompanyTO;

public class CarteiraItemTO implements Comparable<CarteiraItemTO> {

	public CarteiraItemTO(String stock) {
		this.stock = stock;
		avgValue = 0D;
		avgQuantity = 0L;
		avgTotalCost = 0D;
		quantity = 0L;
		negotiations = new ArrayList<NegotiationTO>();
		incomes = new ArrayList<IncomeTO>();
		totalValue = 0D;
		totalIncomeValue = 0D;
		totalCalculateResult = 0D;
	}

	private String stock;

	private Long quantity;

	private Double avgValue;

	private Long avgQuantity;

	private Double avgTotalCost;

	private Double actualValue;

	private Double actualDY;

	private Double buyDY;

	/** Summaring */

	private Double percentActAvgValue;

	private Double totalValue;

	private Double totalActual;

	private Double totalIncomeValue;

	private Double resultPlusIncome;

	private Double totalCalculateResult;

	private Double totalPercentPos;
	
	private List<NegotiationTO> negotiations;

	private List<IncomeTO> incomes;

	private IncomeCompanyTO lastIncomeCompany;

	public void addNegotiation(NegotiationTO neg) {
		// Add to the list
		negotiations.add(neg);

		// But if calculated, ignore to avg value
		if (neg.getCalculated()) {
			if (neg.getCalculateValue() != null) {
				totalCalculateResult += neg.getCalculateValue();	
			}
		} else {
			// AVG Value considers buyer stocks only
			if (neg.getNegotiationType().equals(NegotiationType.Compra)) {
				Double thisTotal = (neg.getQuantity() * neg.getValue()); 
				avgValue = ((avgValue * avgQuantity) + thisTotal) / (avgQuantity + neg.getQuantity());
				BigDecimal avgBD = new BigDecimal(avgValue).setScale(2, RoundingMode.HALF_EVEN);
				avgValue = avgBD.doubleValue();
				avgQuantity += neg.getQuantity();
				avgTotalCost += neg.getCosts();
			}
		}

		if (neg.getNegotiationType().equals(NegotiationType.Compra)) {
			quantity += neg.getQuantity();
		} else {
			if (!neg.getCalculated()) {
				avgTotalCost -= (avgTotalCost / quantity) * neg.getQuantity();
				avgQuantity -= neg.getQuantity();				
			}
			quantity -= neg.getQuantity();
		}

	}

	public void addIncome(IncomeTO income) {
		incomes.add(income);
		totalIncomeValue += income.getValue();
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Double getAvgValue() {
		return avgValue;
	}

	public void setAvgValue(Double avgValue) {
		this.avgValue = avgValue;
	}

	public Double getTotalValue() {
		totalValue = quantity * avgValue;
		return totalValue;
	}

	public List<NegotiationTO> getNegotiations() {
		return negotiations;
	}

	public void setNegotiations(List<NegotiationTO> negotiations) {
		this.negotiations = negotiations;
	}

	public List<IncomeTO> getIncomes() {
		return incomes;
	}

	public void setIncomes(List<IncomeTO> incomes) {
		this.incomes = incomes;
	}

	public Double getTotalIncomeValue() {
		return totalIncomeValue;
	}

	public Double getTotalActual() {
		totalActual = quantity * actualValue;
		return totalActual;
	}

	public Double getResultPlusIncome() {
		resultPlusIncome = getTotalIncomeValue() + getTotalCalculateResult();

		return resultPlusIncome;
	}

	public Double getActualValue() {
		return actualValue;
	}

	public void setActualValue(Double actualValue) {
		this.actualValue = actualValue;
	}

	public Double getTotalCalculateResult() {
		return totalCalculateResult;
	}

	public Long getAvgQuantity() {
		return avgQuantity;
	}

	public void setAvgQuantity(Long avgQuantity) {
		this.avgQuantity = avgQuantity;
	}

	public Double getAvgTotalCost() {
		return avgTotalCost;
	}

	public void setAvgTotalCost(Double avgTotalCost) {
		this.avgTotalCost = avgTotalCost;
	}

	public Double getPercentActAvgValue() {
		percentActAvgValue = (actualValue / avgValue) - 1;

		return percentActAvgValue;
	}

	public void setPercentActAvgValue(Double percentActAvgValue) {
		this.percentActAvgValue = percentActAvgValue;
	}

	public Double getTotalPercentPos() {
		totalPercentPos = ((getTotalActual() + getTotalIncomeValue() + getTotalCalculateResult()) / getTotalValue()) - 1;

		return totalPercentPos;
	}

	public void setTotalPercentPos(Double totalPercentPos) {
		this.totalPercentPos = totalPercentPos;
	}

	public IncomeCompanyTO getLastIncomeCompany() {
		return lastIncomeCompany;
	}

	public void setLastIncomeCompany(IncomeCompanyTO lastIncomeCompany) {
		this.lastIncomeCompany = lastIncomeCompany;
	}

	public Double getActualDY() {
		return actualDY;
	}

	public void setActualDY(Double actualDY) {
		this.actualDY = actualDY;
	}

	public Double getBuyDY() {
		return buyDY;
	}

	public void setBuyDY(Double buyDY) {
		this.buyDY = buyDY;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stock == null) ? 0 : stock.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarteiraItemTO other = (CarteiraItemTO) obj;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CarteiraItemTO [stock=" + stock + ", quantity=" + quantity
				+ ", avgValue=" + avgValue + "]";
	}

	@Override
	public int compareTo(CarteiraItemTO arg0) {
        int lastCmp = stock.compareTo(arg0.stock);
        return (lastCmp != 0 ? lastCmp : stock.compareTo(arg0.stock));
	}

}
