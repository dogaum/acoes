/**
 * 
 */
package br.com.dabage.investments.carteira;

import java.util.Date;

/**
 * @author XPTO
 *
 */
public class StatementVO {

	private Date date;

	private String stock;

	private String statementType;

	private Double value;

	private Double quote;

	private Double costs;

	private Double avgPrice;

	private Long quantity;

	private Long quantityPortfolio;

	private Double amount;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getStatementType() {
		return statementType;
	}

	public void setStatementType(String statementType) {
		this.statementType = statementType;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getCosts() {
		return costs;
	}

	public void setCosts(Double costs) {
		this.costs = costs;
	}

	public Double getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(Double avgPrice) {
		this.avgPrice = avgPrice;
	}

	public Long getQuantityPortfolio() {
		return quantityPortfolio;
	}

	public void setQuantityPortfolio(Long quantityPortfolio) {
		this.quantityPortfolio = quantityPortfolio;
	}

	public Double getQuote() {
		return quote;
	}

	public void setQuote(Double quote) {
		this.quote = quote;
	}

}
