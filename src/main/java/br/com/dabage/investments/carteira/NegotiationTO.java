package br.com.dabage.investments.carteira;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.dabage.investments.config.StockTypeTO;
import br.com.dabage.investments.repositories.AbstractDocument;

@Document(collection="negotiations")
public class NegotiationTO extends AbstractDocument implements Comparable<NegotiationTO> {

	@Indexed
	private String stock;

	private Double value;

	private Date dtNegotiation;

	private Long quantity;

	private NegotiationType negotiationType;

	private Double costs;

	/** Object creation date */
	private Date addDate;

	/** Object remove date */
	private Date removeDate;

	/** Avg value considered for calcs */
	private Double avgBuyValue;

	/** If this negotiation was calculated */
	private Boolean calculated;

	/** Calc date */
	private Date calculateDate;

	/** Result Value of de Sell */
	private Double calculateValue;

	@Indexed
	private BigInteger idCarteira;

	private StockTypeTO stockType;

	/** If this negotiation is a day trade */
	private Boolean dayTrade;

	private Double feeDayTrade;

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

	public Date getDtNegotiation() {
		return dtNegotiation;
	}

	public void setDtNegotiation(Date dtNegotiation) {
		this.dtNegotiation = dtNegotiation;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public NegotiationType getNegotiationType() {
		return negotiationType;
	}

	public void setNegotiationType(NegotiationType negotiationType) {
		this.negotiationType = negotiationType;
	}

	public Double getCosts() {
		return costs;
	}

	public void setCosts(Double costs) {
		this.costs = costs;
	}

	public BigInteger getIdCarteira() {
		return idCarteira;
	}

	public void setIdCarteira(BigInteger idCarteira) {
		this.idCarteira = idCarteira;
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

	public StockTypeTO getStockType() {
		return stockType;
	}

	public void setStockType(StockTypeTO stockType) {
		this.stockType = stockType;
	}

	public Boolean getCalculated() {
		return calculated;
	}

	public void setCalculated(Boolean calculated) {
		this.calculated = calculated;
	}

	public Date getCalculateDate() {
		return calculateDate;
	}

	public void setCalculateDate(Date calculateDate) {
		this.calculateDate = calculateDate;
	}

	public Double getAvgBuyValue() {
		return avgBuyValue;
	}

	public void setAvgBuyValue(Double avgBuyValue) {
		this.avgBuyValue = avgBuyValue;
	}

	public Double getCalculateValue() {
		return calculateValue;
	}

	public void setCalculateValue(Double calculateValue) {
		this.calculateValue = calculateValue;
	}

	public Boolean getDayTrade() {
		return dayTrade;
	}

	public void setDayTrade(Boolean dayTrade) {
		this.dayTrade = dayTrade;
	}

	public Double getFeeDayTrade() {
		return feeDayTrade;
	}

	public void setFeeDayTrade(Double feeDayTrade) {
		this.feeDayTrade = feeDayTrade;
	}

	@Override
	public String toString() {
		return "Movimentacao [Carteira id: " + idCarteira + " Codigo: " + stock + " Valor: " + value
				+ " Data: " + dtNegotiation + " Qtde: " + quantity
				+ " Tipo: " + negotiationType + " Custos: " + costs
				+ "]";
	}

	@Override
	public int compareTo(NegotiationTO o) {
        int lastCmp = dtNegotiation.compareTo(o.dtNegotiation);
        return (lastCmp != 0 ? lastCmp : dtNegotiation.compareTo(o.dtNegotiation));
	}

}
