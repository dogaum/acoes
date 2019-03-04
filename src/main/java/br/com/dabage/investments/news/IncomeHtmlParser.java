/**
 * 
 */
package br.com.dabage.investments.news;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.com.dabage.investments.utils.DateUtils;

/**
 * @author dogaum
 *
 */
public class IncomeHtmlParser {

	static NumberFormat numberFormat = new DecimalFormat ("#,##0.00", new DecimalFormatSymbols (new Locale ("pt", "BR")));

	private Document doc;
	private String fundName;
	private String fundCnpj;
	private String admName;
	private String admCnpj;
	private String responsible;
	private String phone;
	private String isin;
	private String ticker;
	
	private String societyAct;
	private Date infoDate;
	private Date incomeDate;
	private Date paymentDate;
	private Double value;
	private Integer year;
	private String referenceDate;

	public IncomeHtmlParser(Document doc) {
		this.doc = doc;
		parseHtmlContent();
	}

	void parseHtmlContent() {
		Elements tables = doc.getElementsByTag("table");
		Element tb0 = tables.get(0);
		Elements trs0 = tb0.getElementsByTag("tr");
		
		Element tr00 = trs0.get(0);
		Elements tds000 = tr00.getElementsByTag("td");
		this.fundName = tds000.get(1).text();
		this.fundCnpj = tds000.get(3).text();

		Element tr01 = trs0.get(1);
		Elements tds010 = tr01.getElementsByTag("td");
		this.admName = tds010.get(1).text();
		this.admCnpj = tds010.get(3).text();

		Element tr02 = trs0.get(2);
		Elements tds020 = tr02.getElementsByTag("td");
		this.responsible = tds020.get(1).text();
		this.phone = tds020.get(3).text();

		Element tr03 = trs0.get(3);
		Elements tds030 = tr03.getElementsByTag("td");
		this.isin = tds030.get(1).text();
		this.ticker = tds030.get(3).text();
		
		Element tb1 = tables.get(1);
		Elements trs1 = tb1.getElementsByTag("tr");
		Element tr11 = trs1.get(1);
		Elements tds100 = tr11.getElementsByTag("td");
		this.societyAct = tds100.get(1).text();

		Element tr12 = trs1.get(2);
		Elements tds120 = tr12.getElementsByTag("td");
		this.infoDate = DateUtils.parseStrToDate(tds120.get(1).text());

		Element tr13 = trs1.get(3);
		Elements tds130 = tr13.getElementsByTag("td");
		this.incomeDate = DateUtils.parseStrToDate(tds130.get(1).text());

		Element tr14 = trs1.get(4);
		Elements tds140 = tr14.getElementsByTag("td");
		this.paymentDate = DateUtils.parseStrToDate(tds140.get(1).text());

		Element tr15 = trs1.get(5);
		Elements tds150 = tr15.getElementsByTag("td");
		try {
			this.value = numberFormat.parse((tds150.get(1).text())).doubleValue();
		} catch (ParseException e) {
		}

		Element tr16 = trs1.get(6);
		Elements tds160 = tr16.getElementsByTag("td");
		this.referenceDate = tds160.get(1).text();
		
		Element tr17 = trs1.get(7);
		Elements tds170 = tr17.getElementsByTag("td");
		this.year = Integer.valueOf(tds170.get(1).text());
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public String getFundName() {
		return fundName;
	}

	public void setFundName(String fundName) {
		this.fundName = fundName;
	}

	public String getFundCnpj() {
		return fundCnpj;
	}

	public void setFundCnpj(String fundCnpj) {
		this.fundCnpj = fundCnpj;
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

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getSocietyAct() {
		return societyAct;
	}

	public void setSocietyAct(String societyAct) {
		this.societyAct = societyAct;
	}

	public Date getInfoDate() {
		return infoDate;
	}

	public void setInfoDate(Date infoDate) {
		this.infoDate = infoDate;
	}

	public Date getIncomeDate() {
		return incomeDate;
	}

	public void setIncomeDate(Date incomeDate) {
		this.incomeDate = incomeDate;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(String referenceDate) {
		this.referenceDate = referenceDate;
	}

}
