package br.com.dabage.investments.config;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.dabage.investments.repositories.AbstractDocument;

/**
 * Represents a StockType (Ie: FII, Stock, Stock Option, etc...)
 * @author dogaum
 *
 */
@Document(collection="stocktypes")
public class StockTypeTO extends AbstractDocument {

	/** Nome */
	@Indexed
	private String name;

	/** Tributacao em % para operacoes normais. */
	private Double irNormal;

	/** Tributacao em % para operacoes em Daytrade. */
	private Double irDayTrade;

	/** Limite de isencao quando possivel. */
	private Double exempt;

	/** Data de criacao do item; */
	private Date addDate;

	/** Data de remocao do item. */
	private Date removeDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getIrNormal() {
		return irNormal;
	}

	public void setIrNormal(Double irNormal) {
		this.irNormal = irNormal;
	}

	public Double getIrDayTrade() {
		return irDayTrade;
	}

	public void setIrDayTrade(Double irDayTrade) {
		this.irDayTrade = irDayTrade;
	}

	public Double getExempt() {
		return exempt;
	}

	public void setExempt(Double exempt) {
		this.exempt = exempt;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Date getRemoveDate() {
		return removeDate;
	}

	public void setRemoveDate(Date removeDate) {
		this.removeDate = removeDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((addDate == null) ? 0 : addDate.hashCode());
		result = prime * result + ((exempt == null) ? 0 : exempt.hashCode());
		result = prime * result
				+ ((irDayTrade == null) ? 0 : irDayTrade.hashCode());
		result = prime * result
				+ ((irNormal == null) ? 0 : irNormal.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((removeDate == null) ? 0 : removeDate.hashCode());
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
		StockTypeTO other = (StockTypeTO) obj;
		if (addDate == null) {
			if (other.addDate != null)
				return false;
		} else if (!addDate.equals(other.addDate))
			return false;
		if (exempt == null) {
			if (other.exempt != null)
				return false;
		} else if (!exempt.equals(other.exempt))
			return false;
		if (irDayTrade == null) {
			if (other.irDayTrade != null)
				return false;
		} else if (!irDayTrade.equals(other.irDayTrade))
			return false;
		if (irNormal == null) {
			if (other.irNormal != null)
				return false;
		} else if (!irNormal.equals(other.irNormal))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (removeDate == null) {
			if (other.removeDate != null)
				return false;
		} else if (!removeDate.equals(other.removeDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StockTypeTO [name=" + name + ", irNormal=" + irNormal
				+ ", irDayTrade=" + irDayTrade + ", exempt=" + exempt
				+ ", addDate=" + addDate + ", removeDate=" + removeDate + "]";
	}

}
