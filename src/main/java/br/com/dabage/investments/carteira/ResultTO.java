package br.com.dabage.investments.carteira;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.dabage.investments.config.StockTypeTO;

public class ResultTO implements Serializable, Comparable<ResultTO> {

	/** */
	private static final long serialVersionUID = 8491680468358158592L;

	public ResultTO (Integer yearMonth, StockTypeTO stockType, Boolean dayTrade) {
		this.yearMonth = yearMonth;
		this.stockType = stockType;
		this.dayTrade = dayTrade;
		negotiations = new ArrayList<NegotiationTO>();
		result = 0D;
		fee = 0D;
		feeAmount = 0D;
		totalSell = 0D;
	}

	private Integer yearMonth;

	private StockTypeTO stockType;

	private List<NegotiationTO> negotiations;

	private Double totalSell;

	private Double result; 

	private Double resultAmount;

	private Double fee;

	private Double feeAmount;

	private Date feePayDate;

	private Boolean dayTrade;

	/** Add a new Negotiation to calc */
	public void addNegotiation(NegotiationTO negotiation) {
		negotiations.add(negotiation);
		result += negotiation.getCalculateValue();
		totalSell += (negotiation.getValue() * negotiation.getQuantity());
	}

	/**
	 * Calc total Fee
	 * @return
	 */
	public Double getTotalFee() {

		if (getFee() + feeAmount < 10D) {
			return 0D;
		} else {
			return getFee() + feeAmount;
		}

	}

	/** Returns a year month formatted */
	public String getYearMonthFormatted() {
		String yearMonthTemp = yearMonth + "";

		String yearMonthFormatted = yearMonthTemp.substring(4, 6) + "/" + yearMonthTemp.substring(0, 4);

		return yearMonthFormatted;
	}

	public int getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(Integer yearMonth) {
		this.yearMonth = yearMonth;
	}

	public StockTypeTO getStockType() {
		return stockType;
	}

	public void setStockType(StockTypeTO stockType) {
		this.stockType = stockType;
	}

	public List<NegotiationTO> getNegotiations() {
		return negotiations;
	}

	public void setNegotiations(List<NegotiationTO> negotiations) {
		this.negotiations = negotiations;
	}

	public Double getTotalSell() {
		return totalSell;
	}

	public void setTotalSell(Double totalSell) {
		this.totalSell = totalSell;
	}

	public Double getResult() {
		return result;
	}

	public void setResult(Double result) {
		this.result = result;
	}

	public Double getFee() {
		if (stockType.getExempt() == null || stockType.getExempt().doubleValue() == 0.0) {
			if (resultAmount > 0) {
				fee = resultAmount * (stockType.getIrNormal() / 100);
			}
		} else {
			if (totalSell > stockType.getExempt()) {
				fee = resultAmount * (stockType.getIrNormal() / 100);
			}
		}

		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public Date getFeePayDate() {
		return feePayDate;
	}

	public void setFeePayDate(Date feePayDate) {
		this.feePayDate = feePayDate;
	}

	public Boolean getDayTrade() {
		return dayTrade;
	}

	public void setDayTrade(Boolean dayTrade) {
		this.dayTrade = dayTrade;
	}

	public Double getResultAmount() {
		return resultAmount;
	}

	public void setResultAmount(Double resultAmount) {
		this.resultAmount = resultAmount;
	}

	public Double getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(Double feeAmount) {
		this.feeAmount = feeAmount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dayTrade == null) ? 0 : dayTrade.hashCode());
		result = prime * result
				+ ((stockType == null) ? 0 : stockType.hashCode());
		result = prime * result
				+ ((yearMonth == null) ? 0 : yearMonth.hashCode());
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
		ResultTO other = (ResultTO) obj;
		if (dayTrade == null) {
			if (other.dayTrade != null)
				return false;
		} else if (!dayTrade.equals(other.dayTrade))
			return false;
		if (stockType == null) {
			if (other.stockType != null)
				return false;
		} else if (!stockType.equals(other.stockType))
			return false;
		if (yearMonth == null) {
			if (other.yearMonth != null)
				return false;
		} else if (!yearMonth.equals(other.yearMonth))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ResultTO [yearMonth=" + yearMonth + ", stockType=" + stockType
				+ ", totalSell=" + totalSell + ", result=" + result
				+ ", resultAmount=" + resultAmount + ", fee=" + fee
				+ ", dayTrade=" + dayTrade + "]";
	}

	@Override
	public int compareTo(ResultTO arg0) {
        int lastCmp = yearMonth.compareTo(arg0.yearMonth);
        return (lastCmp != 0 ? lastCmp : yearMonth.compareTo(arg0.yearMonth));
	}

}
