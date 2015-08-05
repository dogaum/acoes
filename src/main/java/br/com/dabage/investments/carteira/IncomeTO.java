package br.com.dabage.investments.carteira;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.dabage.investments.repositories.AbstractDocument;

@Document(collection="incomes")
public class IncomeTO extends AbstractDocument {

	@Indexed
	private String stock;

	private Double value;

	private String type;

	private Date incomeDate;

	private Date addDate;

	@Indexed
	private BigInteger idCarteira;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getIncomeDate() {
		return incomeDate;
	}

	public void setIncomeDate(Date incomeDate) {
		this.incomeDate = incomeDate;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public BigInteger getIdCarteira() {
		return idCarteira;
	}

	public void setIdCarteira(BigInteger idCarteira) {
		this.idCarteira = idCarteira;
	}

	@Override
	public String toString() {
		return "IncomeTO [stock=" + stock + ", value=" + value + ", type="
				+ type + ", incomeDate=" + incomeDate + ", addDate=" + addDate
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((addDate == null) ? 0 : addDate.hashCode());
		result = prime * result
				+ ((idCarteira == null) ? 0 : idCarteira.hashCode());
		result = prime * result
				+ ((incomeDate == null) ? 0 : incomeDate.hashCode());
		result = prime * result + ((stock == null) ? 0 : stock.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		IncomeTO other = (IncomeTO) obj;
		if (addDate == null) {
			if (other.addDate != null)
				return false;
		} else if (!addDate.equals(other.addDate))
			return false;
		if (idCarteira == null) {
			if (other.idCarteira != null)
				return false;
		} else if (!idCarteira.equals(other.idCarteira))
			return false;
		if (incomeDate == null) {
			if (other.incomeDate != null)
				return false;
		} else if (!incomeDate.equals(other.incomeDate))
			return false;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
