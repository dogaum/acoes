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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
import br.com.dabage.investments.utils.DateUtils;

import com.itextpdf.text.pdf.PdfReader;

@Component
public class CheckNews {

	private Logger log = Logger.getLogger(CheckNews.class);

	@Autowired
	private GetQuotation getQuotation;

	@Resource
	private NewsRepository newsRepository;

	@Resource
	private CompanyRepository companyRepository;

	@Resource
	private IncomeCompanyRepository incomeCompanyRepository;

	static DecimalFormat percentFormat = new DecimalFormat ("#,##0.00", new DecimalFormatSymbols (new Locale ("pt", "BR")));
	
	static NumberFormat numberFormat = new DecimalFormat ("#,##0.00", new DecimalFormatSymbols (new Locale ("pt", "BR")));

	static NumberFormat numberFormatIncome = new DecimalFormat ("#,##0.000000", new DecimalFormatSymbols (new Locale ("pt", "BR")));
	
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
								log.error("Nao encontrou: " + newsBean.getTicker());
							}
						}
					}

				} catch (Exception e) {
					log.error(e);
				}
			}
		
			cal.add(Calendar.MONTH, 1);
		}

	}
	
	public int run(String query, NewsFilterType ft, String sDate, String fDate, int pages) {

		int qtyNews = 0;
		StringBuffer endFII = new StringBuffer();
		endFII.append("http://www2.bmfbovespa.com.br/Agencia-Noticias/ListarNoticias.aspx?idioma=pt-br");
		// Query
		endFII.append("&q=");
		endFII.append(query);
		// Filter type
		endFII.append("&tipoFiltro=");
		endFII.append(ft.getKey());

		if (ft.equals(NewsFilterType.INTERVAL)) {
			endFII.append("&periodoDe=");
			endFII.append(sDate);
			endFII.append("&periodoAte=");
			endFII.append(fDate);
		}

		String prefix = "http://www2.bmfbovespa.com.br/Agencia-Noticias/";

		for (int i = pages; i >= 1; i--) {
			String finalUrl = endFII.toString() + "&pg=" + i;
			try {
				Connection connection = Jsoup.connect(finalUrl);
				connection.ignoreHttpErrors(true);
				connection.timeout(30000);

				Document doc = connection.get();
				Element pagina = doc.getElementById("linksNoticias");
				if (pagina != null) {
					Elements links = pagina.getElementsByTag("li");
					for (Element link : links) {
						try {
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

								try {
									checkInformeMensal(newsBean);	
								} catch (Exception e) {
									e.printStackTrace();
								}

								SendMailSSL.send(
										newsName,
										getQuotationsByPrefix(newsBean.getTicker(),
												income)
												+ news.text()
												+ "\n\n"
												+ newsBean.getNewLink(),
										newsBean.getAttached());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}

			} catch (Exception e) {
				log.error(e);
			}
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
		percentFormat.setPositivePrefix("+");
		percentFormat.setNegativePrefix("-");

		CompanyTO company = companyRepository.findByTicker(prefix + "11");
		if (company == null) {
			company = companyRepository.findByTicker(prefix + "11B");
		}

		if (company == null) {
			log.error("Nao achou a empresa de ticker prefix: " + prefix);
			return result.toString();
		}
		
		String ticker = company.getTicker();
		Double lastQuote = getQuotation.getLastQuote(ticker);
		if (lastQuote != null) {
			result.append(ticker);
			result.append(" : ");
			result.append("R$ " + numberFormat.format(lastQuote));
			result.append("\n");
		}

		if (income != null) {
			result.append("Rendimento R$ " + numberFormatIncome.format(income));
			result.append("\n");

			if (lastQuote != null) {
				result.append("DY: ");
				Double dy = (income / lastQuote) * 100;
				result.append(numberFormat.format(dy) + " % a.m.");
				result.append(" / " + numberFormat.format(dy * 12) + " % a.a.");
				result.append("\n\n");
			}

			ArrayList<IncomeCompanyTO> list = new ArrayList<IncomeCompanyTO>();
			int count = -13;
			while (count < 0) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, count);
				IncomeCompanyTO incomeCompany = incomeCompanyRepository.findByStockAndYearMonth(ticker, DateUtils.getYearMonth(cal.getTime()));
				if (incomeCompany != null) {
					list.add(incomeCompany);
				}
				count++;
			}
			result.append("\n Historico: \n");
			IncomeCompanyTO incomeBefore = null;
			for (IncomeCompanyTO incomeActual : list) {
				if (incomeBefore != null) {
					Double incBefore = incomeBefore.getValue();
					Double incActual = incomeActual.getValue();
					result.append("Rendimento " + DateUtils.formatToMonthYear(incomeActual.getYearMonth()));
					result.append(": R$ " + numberFormatIncome.format(incActual));
					Double dy = (((incActual / incBefore) -1 ) * 100);
					result.append(" " + percentFormat.format(dy) + "%");
					result.append("\n");
				} else {
					result.append("Rendimento " + DateUtils.formatToMonthYear(incomeActual.getYearMonth()));
					result.append(": R$ " + numberFormatIncome.format(incomeActual.getValue()));
					result.append("\n");
				}
				incomeBefore = incomeActual;
			}

		}
		result.append("\n");
		result.append("\n");
		return result.toString();
	}

	public Double checkIncome(NewsTO newsTO) {
		Double income = null;
		if (newsTO.getNewsHeader().toLowerCase().contains("aviso aos cotistas")) {

			int indexIni = newsTO.getNews().indexOf("http");
			int indexFin = newsTO.getNews().lastIndexOf("flnk");
			String link = newsTO.getNews().substring(indexIni, indexFin-1);
			link = link.replaceFirst("https", "http");
			link = link.replaceAll("visualizarDocumento", "exibirDocumento");

			newsTO.setNewLink(link);
			Connection connection = Jsoup.connect(link);
			connection.ignoreHttpErrors(true);
			connection.timeout(30000);

			Document doc = null;
			try {
				//File file = new File("/tmp/Rendimento.html");
				doc = connection.get();
				//FileUtils.write(file, doc.html(), "UTF-8");
				//newsTO.setAttached(file);
			} catch (IOException e1) {
			}

			IncomeHtmlParser htmlParser = new IncomeHtmlParser(doc);
			income = htmlParser.getValue();
			
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

				// Update company information
				company.setCnpj(htmlParser.getFundCnpj());
				company.setAdmName(htmlParser.getAdmName());
				company.setAdmCnpj(htmlParser.getAdmCnpj());
				company.setResponsible(htmlParser.getResponsible());
				company.setPhone(htmlParser.getPhone());
				company.setIsin(htmlParser.getIsin());
				companyRepository.save(company);
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
					File file = new File("/tmp/Anexo.pdf");
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
						File file = new File("/tmp/Anexo.html");
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
			log.error(e);
			SendMailSSL.send("Erro: Proventos Ex-" + dateFormat.format(new Date()), buffer.toString() + e.getMessage(), null);
		}
	}

	public void checkInformeMensal(NewsTO newsTO) {
		if (newsTO.getNewsHeader().toLowerCase().contains("informe mensal")
				&& !newsTO.getNewsHeader().toLowerCase().contains("(C)")) {

			CompanyTO company = companyRepository.findByTicker(newsTO.getTicker() + "11");
			if (company == null) {
				company = companyRepository.findByTicker(newsTO.getTicker() + "11B");
			}

			if (company == null) {
				return;
			}

			int indexIni = newsTO.getNews().indexOf("http");
			int indexFin = newsTO.getNews().lastIndexOf("flnk");
			String link = newsTO.getNews().substring(indexIni, indexFin-1);
			link = link.replaceFirst("https", "http");
			link = link.replaceAll("visualizarDocumento", "exibirDocumento");

			newsTO.setNewLink(link);
			Connection connection = Jsoup.connect(link);
			connection.ignoreHttpErrors(true);
			connection.timeout(30000);

			Document doc = null;
			try {
				doc = connection.get();
			} catch (IOException e1) {
			}

			InformeMensalParser htmlParser = new InformeMensalParser(doc);
			System.out.println("Fundo: " + newsTO.getTicker());
			System.out.println("Data: " + DateUtils.formatDateToStr(htmlParser.getInfoDate()));
			System.out.println("Cotistas: " + htmlParser.getQtdCotistas());
			System.out.println("Ativo: " + htmlParser.getAtivo());
			System.out.println("Patrimonio: " + htmlParser.getPatrimonioLiquido());
			System.out.println("Cotas: " + htmlParser.getQtdCotas());
			System.out.println("VP: " + htmlParser.getVp());
			System.out.println("Total Disponibilidade: " + htmlParser.getTotalDisponibilidade());
			System.out.println("Total Investido: " + htmlParser.getTotalInvestido());
			System.out.println("Total a Receber: " + htmlParser.getTotalAReceber());
			System.out.println("Receber Aluguel: " + htmlParser.getReceberAluguel());
			System.out.println("Receber Venda: " + htmlParser.getReceberVenda());
			System.out.println("Receber Outros: " + htmlParser.getReceberOutros());
			System.out.println("Rendimentos A Distribuir: " + htmlParser.getRendimentosADistribuir());
			System.out.println("Taxa Adm A Pagar: " + htmlParser.getTaxaAdmAPagar());
			System.out.println("Taxa Performance A Pagar: " + htmlParser.getTaxaPerformanceAPagar());
			System.out.println("Outros Valores A Pagar: " + htmlParser.getOutrosValoresAPagar());
			System.out.println("Total Passivo: " + htmlParser.getTotalPassivo());
			System.out.println();

			company.setQtdCotas(htmlParser.getQtdCotas());
			company.setQtdCotistas(htmlParser.getQtdCotistas());
			company.setAtivo(htmlParser.getAtivo());
			company.setVp(htmlParser.getVp());
			company.setTotalDisponibilidade(htmlParser.getTotalDisponibilidade());
			companyRepository.save(company);
		}
	}
}