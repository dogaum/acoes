package br.com.dabage.investments.company;

import java.util.Date;
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
	private String category;
	private String classification;
	private String cnpj;
	private String admName;
	private String admCnpj;
	private String responsible;
	private String phone;
	private String isin;
	private String setor;
	private Double vp;
	private Long qtdCotistas;
	private Long qtdCotas;
	private Double ativo;
	private Double totalDisponibilidade;
	private Double lastQuote;
	private Date lastUpdate;

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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getAdmName() {
		return admName;
	}

	public void setAdmName(String admName) {
		this.admName = admName;
	}

	public String getAdmCnpj() {
		return admCnpj;
	}

	public void setAdmCnpj(String admCnpj) {
		this.admCnpj = admCnpj;
	}

	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public String getSetor() {
		return setor;
	}

	public void setSetor(String setor) {
		this.setor = setor;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public Double getVp() {
		return vp;
	}

	public void setVp(Double vp) {
		this.vp = vp;
	}

	public Long getQtdCotistas() {
		return qtdCotistas;
	}

	public void setQtdCotistas(Long qtdCotistas) {
		this.qtdCotistas = qtdCotistas;
	}

	public Long getQtdCotas() {
		return qtdCotas;
	}

	public void setQtdCotas(Long qtdCotas) {
		this.qtdCotas = qtdCotas;
	}

	public Double getAtivo() {
		return ativo;
	}

	public void setAtivo(Double ativo) {
		this.ativo = ativo;
	}

	public Double getTotalDisponibilidade() {
		return totalDisponibilidade;
	}

	public void setTotalDisponibilidade(Double totalDisponibilidade) {
		this.totalDisponibilidade = totalDisponibilidade;
	}

	public Double getLastQuote() {
		return lastQuote;
	}

	public void setLastQuote(Double lastQuote) {
		this.lastQuote = lastQuote;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "CompanyBean [prefix=" + prefix + ", ticker=" + ticker
				+ ", name=" + name + ", fullName=" + fullName + ", Category=" + category + ", Setor=" + setor + "]";
	}

}
