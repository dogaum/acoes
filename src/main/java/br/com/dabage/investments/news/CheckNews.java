package br.com.dabage.investments.news;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.company.CompanyTO;
import br.com.dabage.investments.company.IncomeCompanyTO;
import br.com.dabage.investments.mail.SendMailSSL;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.IncomeCompanyRepository;
import br.com.dabage.investments.repositories.NewsRepository;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

@Component
public class CheckNews {

	@Autowired
	private GetQuotation getQuotation;

	@Resource
	private NewsRepository newsRepository;

	@Resource
	private CompanyRepository companyRepository;

	@Resource
	private IncomeCompanyRepository incomeCompanyRepository;

	static NumberFormat numberFormat = new DecimalFormat ("#,##0.00", new DecimalFormatSymbols (new Locale ("pt", "BR")));

	static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	static DateFormat dateFormatSearch = new SimpleDateFormat("yyyy-MM-dd");

	public void run2() {
		String prefix = "http://www2.bmfbovespa.com.br/Agencia-Noticias/";

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2015);
		cal.set(Calendar.MONTH, 5);

		while(cal.getTime().before(new Date())) {
			String endFII = "http://www2.bmfbovespa.com.br/Agencia-Noticias/ListarNoticias.aspx?idioma=pt-br&q=fii&tipoFiltro=3&periodoDe=INICIO&periodoAte=FIM&pg=";

			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
			String INICIO = dateFormatSearch.format(cal.getTime());
			endFII = endFII.replaceFirst("INICIO", INICIO);

			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			String FIM = dateFormatSearch.format(cal.getTime());
			endFII = endFII.replaceFirst("FIM", FIM);

			int pages = 20;
			for (int i = 1; i < pages; i++) {
				try {
					Connection connection = Jsoup.connect(endFII + i);
					connection.ignoreHttpErrors(true);
					connection.timeout(30000);

					Document doc = connection.get();
					
					Element pagina = doc.getElementById("linksNoticias");
					if (pagina != null) {
						Elements links = pagina.getElementsByTag("li");
						for (Element link : links) {
							String href = link.getElementsByTag("a").get(0).attr("href");
							String newsName = link.text();

							doc = Jsoup.connect(prefix + href).get();
							Element news = doc.getElementById("contentNoticia");

							NewsTO newsBean = new NewsTO();
							newsBean.setNewsHeader(newsName.substring(19));
							newsBean.setNewsDate(getDateFromNews(newsName));
							newsBean.setStockType(getStockType(newsBean.getNewsHeader()));
							newsBean.setTicker(getStockTicker(newsBean.getNewsHeader()));
							newsBean.setNews(news.text());
							newsBean.setNewsHref(prefix + href);

							Double income = checkIncome(newsBean);
							if (income == null) {
								System.out.println("Nao encontrou: " + newsBean.getTicker());
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
			cal.add(Calendar.MONTH, 1);
		}

	}
	
	public int run(String query) {

		int qtyNews = 0;

		String endFII = "http://www2.bmfbovespa.com.br/Agencia-Noticias/ListarNoticias.aspx?idioma=pt-br&q=" + query + "&tipoFiltro=0";
		String prefix = "http://www2.bmfbovespa.com.br/Agencia-Noticias/";

		try {
			Connection connection = Jsoup.connect(endFII);
			connection.ignoreHttpErrors(true);
			connection.timeout(30000);

			Document doc = connection.get();
			Element pagina = doc.getElementById("linksNoticias");
			if (pagina != null) {
				Elements links = pagina.getElementsByTag("li");
				for (Element link : links) {
					String href = link.getElementsByTag("a").get(0).attr("href");
					String newsName = link.text();

					connection = Jsoup.connect(prefix + href);
					connection.ignoreHttpErrors(true);
					connection.timeout(30000);
					doc = connection.get();
					Element news = doc.getElementById("contentNoticia");

					NewsTO newsBean = new NewsTO();
					newsBean.setNewsHeader(newsName.substring(19));
					newsBean.setNewsDate(getDateFromNews(newsName));
					newsBean.setStockType(getStockType(newsBean.getNewsHeader()));
					newsBean.setTicker(getStockTicker(newsBean.getNewsHeader()));
					newsBean.setNews(news.text());
					newsBean.setNewsHref(prefix + href);

					if (insertNews(newsBean) && newsBean.getNewsHeader().toLowerCase().contains("fii")) {
						qtyNews++;
						Double income = checkIncome(newsBean);
						SendMailSSL.send(
								newsName,
								getQuotationsByPrefix(newsBean.getTicker(),
										income)
										+ news.text()
										+ "\n\n"
										+ newsBean.getNewLink(),
								newsBean.getAttached());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return qtyNews;
	}

	private boolean insertNews(NewsTO newsTO) {
		NewsTO obj = newsRepository.findByNewsHeaderAndNewsDate(newsTO.getNewsHeader(), newsTO.getNewsDate());
		if (obj == null) {
			newsRepository.save(newsTO);
			return true;
		}

		return false;
	}

	private String getDateFromNews(String newsHeader) {
		String dateStr = newsHeader.substring(0, 16);
		return dateStr;
	}

	private String getStockType(String newsHeader) {
		if (newsHeader.substring(0, 3).equals("FII")) {
			return "FII";
		}
		return "ACOES";
	}

	private String getStockTicker(String newsHeader) {
		String ticker = "";
		int initialIndex = newsHeader.indexOf("(");
		ticker = newsHeader.substring(initialIndex+1, initialIndex + 5);

		return ticker;
	}

	private String getQuotationsByPrefix(String prefix, Double income) {
		StringBuffer result = new StringBuffer();

		CompanyTO company = companyRepository.findByTicker(prefix + "11");
		if (company == null) {
			company = companyRepository.findByTicker(prefix + "11B");
		}

		if (company == null) {
			System.out.println("Nao achou a empresa de ticker" + prefix);
			return result.toString();
		}
		
		String ticker = company.getTicker();
		Double lastQuote = getQuotation.getLastQuote(ticker);
		if (lastQuote != null) {
			result.append("Ultimo negocio:\n");
			result.append(ticker);
			result.append(" : ");
			result.append("R$ " + numberFormat.format(lastQuote));
			result.append("\n");
		}

		if (income != null) {
			result.append("Rendimento R$ " + numberFormat.format(income));
			result.append("\n");
			result.append("DY: ");
			Double dy = (income / lastQuote) * 100;
			result.append(numberFormat.format(dy) + " % a.m.");
			result.append(" / " + numberFormat.format(dy * 12) + " % a.a.");
		}
		result.append("\n");
		result.append("\n");
		return result.toString();
	}

	private Double getIncome(String line) {
		Double income = null;
		int ini = line.indexOf("R$");
		line = line.substring(ini).trim();
		String[] array2 = line.split(" ");
		for (int j = 0; j < array2.length; j++) {
			String linha2 = array2[j];
			linha2 = linha2.replaceAll("[^\\d+(\\.\\,\\d+)?]", "");
			if (linha2.isEmpty() || linha2.length() < 2) {
				continue;
			}
			char lastChar = linha2.charAt(linha2.length() -1);
			while (!Character.isDigit(lastChar)) {
				linha2 = linha2.substring(0, linha2.length() - 1);
				lastChar = linha2.charAt(linha2.length() -1);
			}
			linha2 = linha2.replace(".", "");
			linha2 = linha2.replace(",", ".");
			
			income = Double.valueOf(linha2);
		}
		
		return income;
	}

	public Double checkIncome(NewsTO newsTO) {
		Double income = null;
		if (newsTO.getNews().contains("rendimento")) {

			int indexIni = newsTO.getNews().indexOf("http");
			int indexFin = newsTO.getNews().lastIndexOf("flnk");
			String linkPdf = newsTO.getNews().substring(indexIni, indexFin-1);
			linkPdf = linkPdf.replaceFirst("https", "http");
			linkPdf = linkPdf.replaceAll("visualizarDocumento", "exibirDocumento");
			
			newsTO.setNewLink(linkPdf);

			PdfReader reader;
			IncomePdfParser parser = null;
			try {
				reader = new PdfReader(linkPdf);
				String dados = PdfTextExtractor.getTextFromPage(reader, 1);
				URL url = new URL(linkPdf);
				File file = new File("/temp/Anexo.pdf");
				FileUtils.copyURLToFile(url, file);
				newsTO.setAttached(file);
				parser = new IncomePdfParser(dados);
				reader.close();
			} catch (IOException e) {
				// Não é PDF, é HTML
				// Pegar o html e enviar por email
				Connection connection = Jsoup.connect(linkPdf);
				connection.ignoreHttpErrors(true);
				connection.timeout(30000);

				try {
					File file = new File("/temp/Anexo.html");
					Document doc = connection.get();
					FileUtils.write(file, doc.html(), "UTF-8");
					newsTO.setAttached(file);
				} catch (IOException e1) {
				}

			}

			income = parser.getValue();

			if (income != null) {
				CompanyTO company = companyRepository.findByTicker(newsTO.getTicker() + "11");
				if (company == null) {
					company = companyRepository.findByTicker(newsTO.getTicker() + "11B");
				}

				IncomeCompanyTO incomeTO = new IncomeCompanyTO();
				Calendar cal = Calendar.getInstance();
				Date incomeDate;
				try {
					incomeDate = dateFormat.parse(newsTO.getNewsDate());
				} catch (ParseException e) {
					incomeDate = new Date();
				}
				cal.setTime(incomeDate);

				incomeTO.setIncomeDate(cal.getTime());
				incomeTO.setValue(income);
				incomeTO.setIdCompany(company.getId());
				incomeTO.setStock(company.getTicker());
				int month = cal.get(Calendar.MONTH) + 1;
				String yearMonth = cal.get(Calendar.YEAR) + StringUtils.leftPad(month + "", 2, "0") ;
				incomeTO.setYearMonth(Integer.parseInt(yearMonth));

				if (incomeCompanyRepository.findByStockAndYearMonth(incomeTO.getStock(), incomeTO.getYearMonth()) == null) {
					incomeCompanyRepository.save(incomeTO);	
				}

			}
		} else {
			// get attachment
			String linkPdf = null;
			if (newsTO.getNews().contains("https")) {

				int indexIni = newsTO.getNews().indexOf("http");
				int indexFin = newsTO.getNews().lastIndexOf("flnk");
				linkPdf = newsTO.getNews().substring(indexIni, indexFin-1);
				linkPdf = linkPdf.replaceFirst("https", "http");
				linkPdf = linkPdf.replaceAll("visualizarDocumento", "exibirDocumento");

				newsTO.setNewLink(linkPdf);

				PdfReader reader;
				try {
					reader = new PdfReader(linkPdf);
					URL url = new URL(linkPdf);
					File file = new File("/temp/Anexo.pdf");
					FileUtils.copyURLToFile(url, file);
					newsTO.setAttached(file);
					reader.close();
				} catch (IOException e) {
					// Não é PDF, é HTML
					// Pegar o html e enviar por email
					Connection connection = Jsoup.connect(linkPdf);
					connection.ignoreHttpErrors(true);
					connection.timeout(30000);

					try {
						File file = new File("/temp/Anexo.html");
						Document doc = connection.get();
						FileUtils.write(file, doc.html(), "UTF-8");
						newsTO.setAttached(file);
					} catch (IOException e1) {
					}
				}
			}
		}

		return income;
	}

	public void checkIncomes() {

		String endFII = "http://www2.bmfbovespa.com.br/Agencia-Noticias/ListarNoticias.aspx?idioma=pt-br&q=proventos%20dos%20emissores&tipoFiltro=0";

		checkIncomes(endFII);

	}

	public void checkIncomesTemp() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2015);
		cal.set(Calendar.MONTH, 5);

		Calendar calNow = Calendar.getInstance();
		calNow.add(Calendar.MONTH, 1);
		
		while(cal.getTime().before(calNow.getTime())) {
			String endFII = "http://www2.bmfbovespa.com.br/Agencia-Noticias/ListarNoticias.aspx?idioma=pt-br&q=proventos%20dos%20emissores&tipoFiltro=3&periodoDe=INICIO&periodoAte=FIM&pg=";
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
			String INICIO = dateFormatSearch.format(cal.getTime());
			endFII = endFII.replaceFirst("INICIO", INICIO);

			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			String FIM = dateFormatSearch.format(cal.getTime());
			endFII = endFII.replaceFirst("FIM", FIM);

			int pages = 3;
			for (int i = 1; i < pages; i++) {
				checkIncomes(endFII + i);
			}

			cal.add(Calendar.MONTH, 1);
		}
	}

	public void checkIncomesByInterval(Date initialDate, Date finalDate) {
		String endFII = "http://www2.bmfbovespa.com.br/Agencia-Noticias/ListarNoticias.aspx?idioma=pt-br&q=proventos%20dos%20emissores&tipoFiltro=3&periodoDe=INICIO&periodoAte=FIM&pg=";
		String INICIO = dateFormatSearch.format(initialDate);
		endFII = endFII.replaceFirst("INICIO", INICIO);

		String FIM = dateFormatSearch.format(finalDate);
		endFII = endFII.replaceFirst("FIM", FIM);

		int pages = 3;
		for (int i = 1; i < pages; i++) {
			checkIncomes(endFII + i);
		}
	}

	private void checkIncomes(String endFII) {
		StringBuffer buffer = new StringBuffer();
		try {
			String prefix = "http://www2.bmfbovespa.com.br/Agencia-Noticias/";
			Connection connection = Jsoup.connect(endFII);
			connection.ignoreHttpErrors(true);
			connection.timeout(30000);

			Document doc = connection.get();
			Element pagina = doc.getElementById("linksNoticias");
			if (pagina != null) {
				Elements links = pagina.getElementsByTag("li");
				for (Element link : links) {
					String href = link.getElementsByTag("a").get(0).attr("href");
					String newsName = link.text();

					connection = Jsoup.connect(prefix + href);
					connection.ignoreHttpErrors(true);
					connection.timeout(30000);
					doc = connection.get();
					Element news = doc.getElementById("contentNoticia");

					NewsTO newsBean = new NewsTO();
					newsBean.setNewsHeader(newsName.substring(19));
					newsBean.setNewsDate(getDateFromNews(newsName));
					newsBean.setNews(news.text());
					newsBean.setNewsHref(prefix + href);
					
					if (!insertNews(newsBean)) {
						continue;
					}

					String total = news.text();
					String[] companies = total.split("EMPRESA: ");
					buffer = new StringBuffer();
					for (int i = 1; i < companies.length; i++) {
						try {
							String company = companies[i];
	
							if (!company.contains("RENDIMENTO")) {
								continue;
							}
	
							String[] lines = company.split("\r");
							String ticker = lines[3].substring(14, 21).trim();
							CompanyTO cmp = companyRepository.findByTicker(ticker);
							// Company does not exist
							if (cmp == null) {
								continue;
							}						
							buffer.append(ticker);
							buffer.append("\n");
	
							String incomeDateStr = lines[3].substring(60, 70);
							buffer.append(incomeDateStr);
							buffer.append("\n");						
	
							if (lines[4].trim().length() < 23) {
								continue;
							}
	
							String income = lines[4].trim().substring(10, 23);
							buffer.append(income);
							buffer.append("\n");						
	
							Double incomeValue = new Double(income.replace(".", "").replace(",", "."));
	
							String paymentDateStr = lines[5].trim().substring(10, 20); 
							buffer.append(paymentDateStr);
							buffer.append("\n\n");
	
							buffer.append(getQuotationsByPrefix(ticker.substring(0, 4), incomeValue));
							buffer.append("========================= \n");
	
							IncomeCompanyTO incomeTO = new IncomeCompanyTO();
							Calendar cal = Calendar.getInstance();
							Date incomeDate = null;
							Date paymentDate = null;
							try {
								incomeDate = dateFormat.parse(incomeDateStr);
								paymentDate = dateFormat.parse(paymentDateStr);
							} catch (ParseException e) {
								incomeDate = new Date();
							}
							cal.setTime(incomeDate);
	
							incomeTO.setIncomeDate(cal.getTime());
							incomeTO.setPaymentDate(paymentDate);
							incomeTO.setValue(incomeValue);
							incomeTO.setIdCompany(cmp.getId());
							incomeTO.setStock(ticker);
							int month = cal.get(Calendar.MONTH) + 1;
							String yearMonth = cal.get(Calendar.YEAR) + StringUtils.leftPad(month + "", 2, "0") ;
							incomeTO.setYearMonth(Integer.parseInt(yearMonth));
	
							IncomeCompanyTO incTemp = incomeCompanyRepository.findByStockAndYearMonth(ticker, incomeTO.getYearMonth());
							if (incTemp != null) {
								incTemp.setPaymentDate(incomeTO.getPaymentDate());
								incTemp.setIncomeDate(incomeTO.getIncomeDate());
								incTemp.setValue(incomeTO.getValue());
								incomeCompanyRepository.save(incTemp);
							} else {
								incomeCompanyRepository.save(incomeTO);	
							}
						} catch (Exception e) {
							// Continue processing
						}
					}
					
					if (!buffer.toString().isEmpty()) {
						// Send Email
						SendMailSSL.send("Proventos Ex-" + dateFormat.format(new Date()), buffer.toString(), null);
					}
				}
			}

		} catch (Exception e) {
			SendMailSSL.send("Erro: Proventos Ex-" + dateFormat.format(new Date()), buffer.toString() + e.getMessage(), null);
			e.printStackTrace();
		}
	}
}