package br.com.dabage.investments.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class IncomeTotal implements Serializable, Comparable<IncomeTotal>  {

	/** */
	private static final long serialVersionUID = 1939759331172372410L;

	public IncomeTotal(String stock) {
		this.stock = stock;
		incomes = new ArrayList<IncomeCompanyTO>();
	}

	private String stock;

	private Integer actualYearMonth;

	private List<IncomeCompanyTO> incomes;

	public Double avg24;

	public Double avg12;

	public Double value1;

	public Double value2;

	public Double value3;

	public Double value4;

	public Double value5;

	public Double value6;

	public Double value7;

	public Double value8;

	public Double value9;

	public Double value10;

	public Double value11;

	public Double value12;

	public Double lastQuote;

	public Double lastPercent;

	/**
	 * Add a new IncomeCompany to allocate
	 * @param income
	 */
	public void addIncome(IncomeCompanyTO income) {
		if (!incomes.contains(income)) {
			incomes.add(income);
		}
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public Double getAvg24() {
		return avg24;
	}

	public void setAvg24(Double avg24) {
		this.avg24 = avg24;
	}

	public Double getAvg12() {
		return avg12;
	}

	public void setAvg12(Double avg12) {
		this.avg12 = avg12;
	}

	public Double getValue1() {
		return value1;
	}

	public void setValue1(Double value1) {
		this.value1 = value1;
	}

	public Double getValue2() {
		return value2;
	}

	public void setValue2(Double value2) {
		this.value2 = value2;
	}

	public Double getValue3() {
		return value3;
	}

	public void setValue3(Double value3) {
		this.value3 = value3;
	}

	public Double getValue4() {
		return value4;
	}

	public void setValue4(Double value4) {
		this.value4 = value4;
	}

	public Double getValue5() {
		return value5;
	}

	public void setValue5(Double value5) {
		this.value5 = value5;
	}

	public Double getValue6() {
		return value6;
	}

	public void setValue6(Double value6) {
		this.value6 = value6;
	}

	public Double getValue7() {
		return value7;
	}

	public void setValue7(Double value7) {
		this.value7 = value7;
	}

	public Double getValue8() {
		return value8;
	}

	public void setValue8(Double value8) {
		this.value8 = value8;
	}

	public Double getValue9() {
		return value9;
	}

	public void setValue9(Double value9) {
		this.value9 = value9;
	}

	public Double getValue10() {
		return value10;
	}

	public void setValue10(Double value10) {
		this.value10 = value10;
	}

	public Double getValue11() {
		return value11;
	}

	public void setValue11(Double value11) {
		this.value11 = value11;
	}

	public Double getValue12() {
		return value12;
	}

	public void setValue12(Double value12) {
		this.value12 = value12;
	}

	public Integer getActualYearMonth() {
		return actualYearMonth;
	}

	public void setActualYearMonth(Integer actualYearMonth) {
		this.actualYearMonth = actualYearMonth;
	}

	public List<IncomeCompanyTO> getIncomes() {
		return incomes;
	}

	public void setIncomes(List<IncomeCompanyTO> incomes) {
		this.incomes = incomes;
	}

	public Double getLastQuote() {
		return lastQuote;
	}

	public void setLastQuote(Double lastQuote) {
		this.lastQuote = lastQuote;
	}

	public Double getLastPercent() {
		return lastPercent;
	}

	public void setLastPercent(Double lastPercent) {
		this.lastPercent = lastPercent;
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
		IncomeTotal other = (IncomeTotal) obj;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		return true;
	}

	@Override
	public int compareTo(IncomeTotal o) {
		return this.stock.compareTo(o.getStock());
	}

}
