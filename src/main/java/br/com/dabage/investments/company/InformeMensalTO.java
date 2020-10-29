package br.com.dabage.investments.company;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import br.com.dabage.investments.repositories.AbstractDocument;

@Document(collection="infomesmensal")
public class InformeMensalTO extends AbstractDocument {

	/** */
	private static final long serialVersionUID = 8161642320789086589L;

	public InformeMensalTO() { }

	private String ticker;

	// Table 1
	private String mesCompetencia;
	private String adm;
	private String cnpjAdm;

	// Table 2 
	private Date infoDate;
	private Long qtdCotistas;
	
	// Table 3
	private Double ativo;
	private Double patrimonioLiquido;
	private Long qtdCotas;
	private Double vp;
	
	// Table 4 - Ativo
	private Double totalDisponibilidade;
	private Double totalInvestido;
	private Double totalAReceber;
	private Double receberAluguel;
	private Double receberVenda;
	private Double receberOutros;
	
	// Table 5 - Passivo
	private Double rendimentosADistribuir;
	private Double taxaAdmAPagar;
	private Double taxaPerformanceAPagar;
	private Double outrosValoresAPagar;
	private Double totalPassivo;

	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public Date getInfoDate() {
		return infoDate;
	}
	public void setInfoDate(Date infoDate) {
		this.infoDate = infoDate;
	}
	public Long getQtdCotistas() {
		return qtdCotistas;
	}
	public void setQtdCotistas(Long qtdCotistas) {
		this.qtdCotistas = qtdCotistas;
	}
	public Double getAtivo() {
		return ativo;
	}
	public void setAtivo(Double ativo) {
		this.ativo = ativo;
	}
	public Double getPatrimonioLiquido() {
		return patrimonioLiquido;
	}
	public void setPatrimonioLiquido(Double patrimonioLiquido) {
		this.patrimonioLiquido = patrimonioLiquido;
	}
	public Long getQtdCotas() {
		return qtdCotas;
	}
	public void setQtdCotas(Long qtdCotas) {
		this.qtdCotas = qtdCotas;
	}
	public Double getVp() {
		return vp;
	}
	public void setVp(Double vp) {
		this.vp = vp;
	}
	public Double getTotalDisponibilidade() {
		return totalDisponibilidade;
	}
	public void setTotalDisponibilidade(Double totalDisponibilidade) {
		this.totalDisponibilidade = totalDisponibilidade;
	}
	public Double getTotalInvestido() {
		return totalInvestido;
	}
	public void setTotalInvestido(Double totalInvestido) {
		this.totalInvestido = totalInvestido;
	}
	public Double getTotalAReceber() {
		return totalAReceber;
	}
	public void setTotalAReceber(Double totalAReceber) {
		this.totalAReceber = totalAReceber;
	}
	public Double getReceberAluguel() {
		return receberAluguel;
	}
	public void setReceberAluguel(Double receberAluguel) {
		this.receberAluguel = receberAluguel;
	}
	public Double getReceberVenda() {
		return receberVenda;
	}
	public void setReceberVenda(Double receberVenda) {
		this.receberVenda = receberVenda;
	}
	public Double getReceberOutros() {
		return receberOutros;
	}
	public void setReceberOutros(Double receberOutros) {
		this.receberOutros = receberOutros;
	}
	public Double getRendimentosADistribuir() {
		return rendimentosADistribuir;
	}
	public void setRendimentosADistribuir(Double rendimentosADistribuir) {
		this.rendimentosADistribuir = rendimentosADistribuir;
	}
	public Double getTaxaAdmAPagar() {
		return taxaAdmAPagar;
	}
	public void setTaxaAdmAPagar(Double taxaAdmAPagar) {
		this.taxaAdmAPagar = taxaAdmAPagar;
	}
	public Double getTaxaPerformanceAPagar() {
		return taxaPerformanceAPagar;
	}
	public void setTaxaPerformanceAPagar(Double taxaPerformanceAPagar) {
		this.taxaPerformanceAPagar = taxaPerformanceAPagar;
	}
	public Double getOutrosValoresAPagar() {
		return outrosValoresAPagar;
	}
	public void setOutrosValoresAPagar(Double outrosValoresAPagar) {
		this.outrosValoresAPagar = outrosValoresAPagar;
	}
	public Double getTotalPassivo() {
		return totalPassivo;
	}
	public void setTotalPassivo(Double totalPassivo) {
		this.totalPassivo = totalPassivo;
	}
	public String getMesCompetencia() {
		return mesCompetencia;
	}
	public void setMesCompetencia(String mesCompetencia) {
		this.mesCompetencia = mesCompetencia;
	}
	public String getAdm() {
		return adm;
	}
	public void setAdm(String adm) {
		this.adm = adm;
	}
	public String getCnpjAdm() {
		return cnpjAdm;
	}
	public void setCnpjAdm(String cnpjAdm) {
		this.cnpjAdm = cnpjAdm;
	}

}
