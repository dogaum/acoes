package br.com.dabage.investments.carteira;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Date;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.dabage.investments.repositories.AbstractDocument;

@Document(collection="portfolioitem")
public class PortfolioItemTO extends AbstractDocument implements Comparable<PortfolioItemTO> {

	/** */
	private static final long serialVersionUID = 3021596721970574596L;

	public PortfolioItemTO(BigInteger idCarteira, String stock) {
		this.idCarteira = idCarteira;
		this.stock = stock;
	}

	@Indexed
	private BigInteger idCarteira;

	/**
	 * Acao/FII (Ex: FLRP11B)
	 */
	@Indexed
	private String stock;

	/**
	 * Quantidade
	 */
	private Long quantity;

	/**
	 * Saldo
	 */
	private Double balance;

	/**
	 * Preco medio
	 */
	private Double avgPrice;

	/**
	 * Resultado
	 */
	private Double result;

	/**
	 * Resultado acumulado
	 */
	private Double accumulatedResult;

	/**
	 * Data de processamento
	 */
	private Date processDate;

	public BigInteger getIdCarteira() {
		return idCarteira;
	}

	public void setIdCarteira(BigInteger idCarteira) {
		this.idCarteira = idCarteira;
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

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(Double avgPrice) {
		this.avgPrice = avgPrice;
	}

	public Double getResult() {
		return result;
	}

	public void setResult(Double result) {
		this.result = result;
	}

	public Double getAccumulatedResult() {
		return accumulatedResult;
	}

	public void setAccumulatedResult(Double accumulatedResult) {
		this.accumulatedResult = accumulatedResult;
	}

	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	@Override
	public int compareTo(PortfolioItemTO o) {
		int lastCmp = processDate.compareTo(o.processDate);
        return (lastCmp != 0 ? lastCmp : processDate.compareTo(o.processDate));
	}

	@Transient
	public void addNegotiation(NegotiationTO negotiation) {
		boolean isFisrt = Boolean.FALSE;
		if (this.quantity == null) {
			isFisrt = Boolean.TRUE;
		}

		this.processDate = new Date();

		Double totalNegotiation = 0D;
		if (negotiation.getNegotiationType().equals(NegotiationType.Compra)) {
			totalNegotiation = (negotiation.getQuantity() * negotiation.getValue()) + negotiation.getCosts();
		} else {
			totalNegotiation = ((negotiation.getQuantity() * negotiation.getValue()) - negotiation.getCosts()) * -1;
		}

		if (isFisrt) {
			this.quantity = negotiation.getQuantity();
			this.balance = totalNegotiation;
			BigDecimal avgPriceBD = new BigDecimal(totalNegotiation / this.quantity).setScale(2, RoundingMode.HALF_EVEN);
			this.avgPrice = avgPriceBD.doubleValue();
			this.result = 0D;
			this.accumulatedResult = 0D;
		} else {
			// Balance is equal anytime
			this.balance = this.balance + totalNegotiation;
			if (negotiation.getNegotiationType().equals(NegotiationType.Compra)) {
				Long quantityBefore = this.quantity;
				Long actualQuantity = this.quantity + negotiation.getQuantity();

				if (actualQuantity.equals(0L)) {
					this.avgPrice = 0D;
				} else {
					this.avgPrice = ((quantityBefore * this.avgPrice) + totalNegotiation) / actualQuantity;
					BigDecimal avgPriceBD = new BigDecimal(this.avgPrice).setScale(2, RoundingMode.HALF_EVEN);
					this.avgPrice = avgPriceBD.doubleValue();					
				}

				this.result = 0D;
				this.accumulatedResult += this.result;

				// assumes actual quantity
				this.quantity = actualQuantity;
			} else {

				this.result = (totalNegotiation *-1) - (negotiation.getQuantity() * this.avgPrice);
				this.accumulatedResult += this.result;

				this.quantity = this.quantity - negotiation.getQuantity();

				// If Sell, does not change avg price or if quantity is 0, then avgPrice = 0
				if (this.quantity.equals(0L)) {
					this.avgPrice = 0D;
				}
			}
		}
	}
	
	@Transient
	public void addAmortization(IncomeTO income) {
		avgPrice -= income.getValue() / quantity;
		balance = avgPrice * quantity;
	}
}
