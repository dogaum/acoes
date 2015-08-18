package br.com.dabage.investments.home;

import java.io.Serializable;

import br.com.dabage.investments.utils.DateUtils;

public class IncomeVO implements Serializable, Comparable<IncomeVO> {

	/** */
	private static final long serialVersionUID = -2137076832864615026L;

	public IncomeVO(String yearMonth) {
		this.yearMonth = yearMonth;
		this.yearMonthFormatted = DateUtils.formatToMonthYear(Integer.parseInt(yearMonth));
	}

	private String yearMonth;

	private Double value;

	private String yearMonthFormatted;

	public void add(Double value) {
		if (this.value == null) {
			this.value = value;
		} else {
			this.value += value;
		}
	}

	public String getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getYearMonthFormatted() {
		return yearMonthFormatted;
	}

	public void setYearMonthFormatted(String yearMonthFormatted) {
		this.yearMonthFormatted = yearMonthFormatted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((yearMonth == null) ? 0 : yearMonth.hashCode());
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
		IncomeVO other = (IncomeVO) obj;
		if (yearMonth == null) {
			if (other.yearMonth != null)
				return false;
		} else if (!yearMonth.equals(other.yearMonth))
			return false;
		return true;
	}

	@Override
	public int compareTo(IncomeVO o) {
		return this.yearMonth.compareTo(o.getYearMonth());
	}

	@Override
	public String toString() {
		return "IncomeVO [yearMonthFormatted=" + yearMonthFormatted
				+ ", value=" + value + "]";
	}

}
