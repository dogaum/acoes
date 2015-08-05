package br.com.dabage.investments.company;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.dabage.investments.repositories.AbstractDocument;

@Document(collection="incomescompanies")
public class IncomeCompanyTO extends AbstractDocument implements Comparable<IncomeCompanyTO> {

	@Indexed
	private String stock;

	private Double value;

	private Date incomeDate;

	private Integer yearMonth;

	@Transient
	private Date yearMonthDate;
	
	@Indexed
	private BigInteger idCompany;

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Date getIncomeDate() {
		return incomeDate;
	}

	public void setIncomeDate(Date incomeDate) {
		this.incomeDate = incomeDate;
	}

	public BigInteger getIdCompany() {
		return idCompany;
	}

	public void setIdCompany(BigInteger idCompany) {
		this.idCompany = idCompany;
	}

	public Integer getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(Integer yearMonth) {
		this.yearMonth = yearMonth;
	}

	public Date getYearMonthDate() {
		return yearMonthDate;
	}

	public void setYearMonthDate(Date yearMonthDate) {
		this.yearMonthDate = yearMonthDate;
		if (yearMonthDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(yearMonthDate);
			int month = cal.get(Calendar.MONTH) + 1;
			String yearMonth = cal.get(Calendar.YEAR) + StringUtils.leftPad(month + "", 2, "0") ;
			this.yearMonth = Integer.parseInt(yearMonth);			
		}
	}

	@Override
	public String toString() {
		return "IncomeCompanyTO [stock=" + stock + ", value=" + value
				+ ", incomeDate=" + incomeDate + ", idCompany=" + idCompany
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((stock == null) ? 0 : stock.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result
				+ ((yearMonth == null) ? 0 : yearMonth.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IncomeCompanyTO other = (IncomeCompanyTO) obj;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (yearMonth == null) {
			if (other.yearMonth != null)
				return false;
		} else if (!yearMonth.equals(other.yearMonth))
			return false;
		return true;
	}

	@Override
	public int compareTo(IncomeCompanyTO o) {
		return this.incomeDate.compareTo(o.getIncomeDate());
	}

	public static Comparator<IncomeCompanyTO> IncomeDateDesc = new Comparator<IncomeCompanyTO>() {

		public int compare(IncomeCompanyTO inc1, IncomeCompanyTO inc2) {

			// ascending order
			return inc2.getIncomeDate().compareTo(inc1.getIncomeDate());
		}

	};
}
