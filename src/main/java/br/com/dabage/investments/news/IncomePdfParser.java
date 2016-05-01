/**
 * 
 */
package br.com.dabage.investments.news;

import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * @author dogaum
 *
 */
public class IncomePdfParser {

	static final String SPLIT = "\n";
	static final String ADMINISTRATOR = "Administrador";
	static final String RESPONSIBLE = "Responsável pela informação";
	static final String PHONE = "Telefone para contato";
	static final String TYPE = "Tipo do Evento";
	static final String INCOMEDATE = "Data-base";
	static final String PAYMENTDATE = "Data do pagamento";
	static final String VALUE = "Valor do Rendimento";
	static final String VALUE_VAM = "Valor bruto distribuído por cota:";
	static final String QUOTEQTY = "Quantidade total de cotas";
	static final String TOTALAMOUNT = "Lucro a ser distribuído";
	static final String REFERENCEDATE = "Período de referência";
	static final String MONET = "R$";

	private String content;

	private String administrator;

	private String responsible;

	private String phone;

	private String type;

	private Date incomeDate;

	private Date paymentDate;

	private Double value;

	private Integer quoteQty;

	private Double totalAmount;

	private Integer referenceDate;

	public IncomePdfParser(String content) {
		this.content = content;
		parsePdfContent();
	}

	void parsePdfContent() {
		String[] data = content.split(SPLIT);
		LinkedList<String> linked = new LinkedList<String>();
		for (int i = 0; i < data.length; i++) {
			String temp = data[i];
			temp = temp.trim();
			if (temp.isEmpty()) {
				continue;
			} else if (temp.startsWith("")) {
				temp = temp.substring(1);
			}
			linked.add(temp.trim());
		}

		ListIterator<String> iterator = linked.listIterator();
		while(iterator.hasNext()) {
			String actual = iterator.next();

			if (actual.contains(VALUE) || actual.contains(VALUE_VAM) || actual.startsWith(MONET)) {
				Double income = parseValue(actual);
				if (income == null) {
					String next = iterator.next();
					income = parseValue(next);
				}

				if (this.value == null) {
					this.value = income;	
				}
			} else if (actual.startsWith(ADMINISTRATOR)) {
				String adm = parseString(actual, ADMINISTRATOR, Boolean.FALSE);
				if (this.administrator == null && adm != null && !adm.isEmpty()) {
					this.administrator = adm;
				}
			} else if (actual.startsWith(TYPE)) {
				String tp = parseString(actual, TYPE, Boolean.FALSE);
				if (this.type == null && tp != null && !tp.isEmpty()) {
					this.type = tp;
				}
			}
		}
		
	}

	private String parseString(String line, String key, Boolean useLike) {
		String result = "";
		if (useLike && line.startsWith(key)) {
			int ini = line.indexOf(key);
			line = line.substring(ini).trim();
			String[] array = line.split(" ");
			result = array[1];
			
		} else if (line.startsWith(key)) {

			result = line.replace(key, "");
		}

		return result;
	}
	
	private Double parseValue(String line) {
		line = line.trim();
		Double value = null;

		if (line.contains(MONET)) {
			int ini = line.indexOf(MONET);
			line = line.substring(ini).trim();
			String[] array2 = line.split(" ");
			for (int j = 0; j < array2.length; j++) {
				value = getValue(array2[j]);
			}
		} else {
			value = getValue(line);
		}

		return value;
	}

	private Double getValue(String linha) {
		linha = linha.replaceAll("[^\\d+(\\.\\,\\d+)?]", "");
		if (linha.isEmpty() || linha.length() < 2) {
			return null;
		}
		char lastChar = linha.charAt(linha.length() -1);
		while (!Character.isDigit(lastChar)) {
			linha = linha.substring(0, linha.length() - 1);
			lastChar = linha.charAt(linha.length() -1);
		}
		
		if (linha.contains(",")) {
			linha = linha.replace(".", "");
			linha = linha.replace(",", ".");				
		}

		value = Double.valueOf(linha);
		
		return value;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAdministrator() {
		return administrator;
	}

	public void setAdministrator(String administrator) {
		this.administrator = administrator;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public Integer getQuoteQty() {
		return quoteQty;
	}

	public void setQuoteQty(Integer quoteQty) {
		this.quoteQty = quoteQty;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Integer referenceDate) {
		this.referenceDate = referenceDate;
	}

}
