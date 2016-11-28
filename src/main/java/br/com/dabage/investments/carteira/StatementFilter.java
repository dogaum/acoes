/**
 * 
 */
package br.com.dabage.investments.carteira;

import java.io.Serializable;
import java.util.Date;

/**
 * @author XPTO
 *
 */
public class StatementFilter implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;
	
	private Date filterInitialDate;
	private Date filterFinalDate;
	private StatementType type;
	private String stock;
	
	public Date getFilterInitialDate() {
		return filterInitialDate;
	}
	public void setFilterInitialDate(Date filterInitialDate) {
		this.filterInitialDate = filterInitialDate;
	}
	public Date getFilterFinalDate() {
		return filterFinalDate;
	}
	public void setFilterFinalDate(Date filterFinalDate) {
		this.filterFinalDate = filterFinalDate;
	}
	public StatementType getType() {
		return type;
	}
	public void setType(StatementType type) {
		this.type = type;
	}
	public String getStock() {
		return (stock != null) ? stock.toUpperCase() : stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
}
