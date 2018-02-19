/**
 * 
 */
package br.com.dabage.investments.news;

import java.util.Date;

import org.jsoup.nodes.Document;

import br.com.dabage.investments.utils.DateUtils;
import br.com.dabage.investments.utils.HtmlParserUtils;

/**
 * @author dogaum
 *
 */
public class InformeMensalParser {

	private Document doc;

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

	public InformeMensalParser(Document doc) {
		this.doc = doc;
		parseHtmlContent();
	}

	void parseHtmlContent() {
		// Table 1 - Ignorar por enqto

		// Table 2
		// Info Date
		String infoDateStr = HtmlParserUtils.getValue(doc, 2, 1, 2);
		this.setInfoDate(DateUtils.parseStrToDate(infoDateStr));

		// Quantidade de cotistas
		String qtdCotistasStr = HtmlParserUtils.getValue(doc, 2, 2, 2);
		this.setQtdCotistas(HtmlParserUtils.parseLong(qtdCotistasStr));

		// Table 3
		String ativoStr = HtmlParserUtils.getValue(doc, 3, 1, 3);
		this.setAtivo(HtmlParserUtils.parseDouble(ativoStr));

		String patrimonioLiquidoStr = HtmlParserUtils.getValue(doc, 3, 2, 3);
		this.setPatrimonioLiquido(HtmlParserUtils.parseDouble(patrimonioLiquidoStr));

		String qtdCotasStr = HtmlParserUtils.getValue(doc, 3, 3, 3);
		this.setQtdCotas(HtmlParserUtils.parseLong(qtdCotasStr));

		String vpStr = HtmlParserUtils.getValue(doc, 3, 4, 3);
		this.setVp(HtmlParserUtils.parseDouble(vpStr));

		// Table 4 - Ativo
		String totalDisponibilidadeStr = HtmlParserUtils.getValue(doc, 4, 2, 3);
		this.setTotalDisponibilidade(HtmlParserUtils.parseDouble(totalDisponibilidadeStr));

		String totalInvestidoStr = HtmlParserUtils.getValue(doc, 4, 7, 3);
		this.setTotalInvestido(HtmlParserUtils.parseDouble(totalInvestidoStr));

		String totalAReceberStr = HtmlParserUtils.getValue(doc, 4, 34, 3);
		this.setTotalAReceber(HtmlParserUtils.parseDouble(totalAReceberStr));

		String receberAluguelStr = HtmlParserUtils.getValue(doc, 4, 35, 3);
		this.setReceberAluguel(HtmlParserUtils.parseDouble(receberAluguelStr));

		String receberVendaStr = HtmlParserUtils.getValue(doc, 4, 36, 3);
		this.setReceberVenda(HtmlParserUtils.parseDouble(receberVendaStr));

		String receberOutrosStr = HtmlParserUtils.getValue(doc, 4, 37, 3);
		this.setReceberOutros(HtmlParserUtils.parseDouble(receberOutrosStr));

		// Table 5 - Passivo
		String rendimentosADistribuirStr = HtmlParserUtils.getValue(doc, 5, 2, 3);
		this.setRendimentosADistribuir(HtmlParserUtils.parseDouble(rendimentosADistribuirStr));

		String taxaAdmAPagarStr = HtmlParserUtils.getValue(doc, 5, 3, 3);
		this.setTaxaAdmAPagar(HtmlParserUtils.parseDouble(taxaAdmAPagarStr));

		String taxaPerformanceAPagarStr = HtmlParserUtils.getValue(doc, 5, 4, 3);
		this.setTaxaPerformanceAPagar(HtmlParserUtils.parseDouble(taxaPerformanceAPagarStr));

		String outrosValoresAPagarStr = HtmlParserUtils.getValue(doc, 5, 11, 3);
		this.setOutrosValoresAPagar(HtmlParserUtils.parseDouble(outrosValoresAPagarStr));

		String totalPassivoStr = HtmlParserUtils.getValue(doc, 5, 12, 3);
		this.setTotalPassivo(HtmlParserUtils.parseDouble(totalPassivoStr));
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
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

}
