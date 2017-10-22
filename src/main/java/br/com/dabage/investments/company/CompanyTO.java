package br.com.dabage.investments.company;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.dabage.investments.config.StockTypeTO;
import br.com.dabage.investments.home.IncomeVO;
import br.com.dabage.investments.repositories.AbstractDocument;

@Document(collection="companies")
public class CompanyTO extends AbstractDocument {

	/** */
	private static final long serialVersionUID = 8161642320789086589L;

	public CompanyTO() { }

	public CompanyTO(String ticker, String name, String fullName) {
		this.ticker = ticker;
		this.name = name;
		this.fullName = fullName;
		this.prefix = ticker.substring(0, 4);
	}

	@Indexed
	private String prefix;
	@Indexed
	private String ticker;
	private String name;
	private String fullName;

	@Transient
	private Map<String, List<IncomeVO>> lastIncomes;

	@DBRef(lazy=true)
	private StockTypeTO stockType;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public StockTypeTO getStockType() {
		return stockType;
	}

	public void setStockType(StockTypeTO stockType) {
		this.stockType = stockType;
	}

	public Map<String, List<IncomeVO>> getLastIncomes() {
		return lastIncomes;
	}

	public void setLastIncomes(Map<String, List<IncomeVO>> lastIncomes) {
		this.lastIncomes = lastIncomes;
	}

	@Override
	public String toString() {
		return "CompanyBean [prefix=" + prefix + ", ticker=" + ticker
				+ ", name=" + name + ", fullName=" + fullName + "]";
	}

}
