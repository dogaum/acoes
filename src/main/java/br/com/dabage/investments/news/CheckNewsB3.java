package br.com.dabage.investments.news;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.cert.CertificateException;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.pdf.PdfReader;

import br.com.dabage.investments.company.CompanyTO;
import br.com.dabage.investments.company.IncomeCompanyTO;
import br.com.dabage.investments.company.InformeMensalTO;
import br.com.dabage.investments.mail.SendMailSSL;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.IncomeCompanyRepository;
import br.com.dabage.investments.repositories.InformeMensalRepository;
import br.com.dabage.investments.repositories.NewsRepository;
import br.com.dabage.investments.utils.DateUtils;

@Component
public class CheckNewsB3 {

	private Logger log = LogManager.getLogger(CheckNewsB3.class);

	@Autowired
	private GetQuotation getQuotation;

	@Autowired
	private NewsRepository newsRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private IncomeCompanyRepository incomeCompanyRepository;

	@Autowired
	private InformeMensalRepository informeMensalRepository;
	
	static DecimalFormat percentFormat = new DecimalFormat ("#,##0.00", new DecimalFormatSymbols (new Locale ("pt", "BR")));
	
	static NumberFormat numberFormat = new DecimalFormat ("#,##0.00", new DecimalFormatSymbols (new Locale ("pt", "BR")));

	static NumberFormat numberFormatIncome = new DecimalFormat ("#,##0.000000", new DecimalFormatSymbols (new Locale ("pt", "BR")));
	
	static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	static DateFormat dateFormatSearch = new SimpleDateFormat("yyyy-MM-dd");

	static DateFormat dateFormatNews = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static DateFormat dateFormatNewsSave = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public Document getDocument(String url) {

		try {
		    /*
		     *  fix for
		     *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
		     *       sun.security.validator.ValidatorException:
		     *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
		     *               unable to find valid certification path to requested target
		     */
		    TrustManager[] trustAllCerts = new TrustManager[] {
		       new X509TrustManager() {
		          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		            return null;
		          }

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}

		       }
		    };

		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		    // Create all-trusting host name verifier
		    HostnameVerifier allHostsValid = new HostnameVerifier() {
		        public boolean verify(String hostname, SSLSession session) {
		          return true;
		        }
		    };
		    // Install the all-trusting host verifier
		    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		    /*
		     * end of the fix
		     */
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Connection connection = Jsoup.connect(url);
		connection.ignoreHttpErrors(true);
		connection.timeout(600000);
		connection.header("Accept-Language", "pt-BR,pt;q=0.8");
		connection.header("Accept-Encoding", "gzip,deflate,sdch");
		connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
		connection.maxBodySize(0);

		Document doc = null;
		try {
			doc = connection.get();
		} catch (IOException e) {
			System.out.println("Erro ao consultar a URL: " + url);
			log.error("Erro ao consultar a URL: " + url);
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 
	 * @param query texto a ser consultado
	 * @param sDate Data Inicial
	 * @param fDate Data final
	 * @return
	 */
	public int run(String query, String sDate, String fDate) {

		String url = "https://sistemasweb.b3.com.br/PlantaoNoticias/Noticias/ListarTitulosNoticias?agencia=18&palavra="+ query + "&dataInicial=" + sDate + "&dataFinal=" + fDate;

		int qtyNews = 0;

		Document doc = this.getDocument(url);
		String result = doc.body().text();

		try {

			JSONArray arr = new JSONArray(result);
			String idNoticia = "";
			String dateTimeNoticia = "";
			String headline = "";
			for(int i = 0; i < arr.length(); i++){
				idNoticia = arr.getJSONObject(i).getJSONObject("NwsMsg").getString("id");
				dateTimeNoticia = arr.getJSONObject(i).getJSONObject("NwsMsg").getString("dateTime");
				headline = arr.getJSONObject(i).getJSONObject("NwsMsg").getString("headline");
				String urlItem = String.format("https://sistemasweb.b3.com.br/PlantaoNoticias/Noticias/Detail?idNoticia=%s&agencia=18&dataNoticia=%s", idNoticia, dateTimeNoticia);
	
				Document docItem = this.getDocument(urlItem);;
				String bodyItem = docItem.getElementById("conteudoDetalhe").getAllElements().text();
				int indexIni = bodyItem.indexOf("http");
				int indexFin = bodyItem.lastIndexOf("flnk");
				String linkPdf = bodyItem.substring(indexIni, indexFin-1);
				linkPdf = linkPdf.replaceFirst("https", "http");
				linkPdf = linkPdf.replaceAll("visualizarDocumento", "exibirDocumento");

				try {

					NewsTO newsBean = new NewsTO();
					newsBean.setNewsHeader(headline);
					newsBean.setNewsDate(dateTimeNoticia);
					newsBean.setStockType(getStockType(newsBean.getNewsHeader()));
					newsBean.setTicker(getStockTicker(newsBean.getNewsHeader()));
					newsBean.setNews(bodyItem);
					newsBean.setNewsHref(linkPdf);
					newsBean.setIdNoticia(idNoticia);

					if ((insertNews(newsBean) || !isEmailSent(newsBean)) && newsBean.getNewsHeader().toLowerCase().contains("fii")) {
						qtyNews++;
						Double income = null;
								
						try {
							income = checkIncome(newsBean);	
						} catch (Exception e) {
							log.error("Erro ao verificar o Aviso aos cotistas " + headline);
							log.error(e);
							e.printStackTrace();									
						}
						newsRepository.save(newsBean);

						Double vp = 0D;
						
						try {
							vp = checkInformeMensal(newsBean);	
						} catch (Exception e) {
							log.error("Erro ao verificar o Informe mensal. " + headline);
							log.error(e);
							e.printStackTrace();
						}

						newsRepository.save(newsBean);

						if (newsBean.getNewsHeader().toLowerCase().contains("rentabilidade")
							|| newsBean.getNewsHeader().toLowerCase().contains("perfil")
							|| newsBean.getNewsHeader().toLowerCase().contains("regulamento")) {
							continue;
						}

						SendMailSSL.send(
								headline,
								getQuotationsByPrefix(newsBean.getTicker(), income, vp)
										+ bodyItem
										+ "\n\n"
										+ newsBean.getNewLink(),
								newsBean.getAttached(),
								"dogaum@gmail.com");
						newsBean.setEmailSent(true);
						newsRepository.save(newsBean);
					}
				} catch (Exception e) {
					log.error(e);
					e.printStackTrace();
				}
				
			}
		} catch (Exception e ) {

		}
		return qtyNews;
	}

	private boolean insertNews(NewsTO newsTO) {
		NewsTO obj = null;
		try {
			obj = newsRepository.findByIdNoticia(newsTO.getIdNoticia());
		} catch (Exception e) {
			// Encontrou inserido duas vezes.
			return false;
		}

		//NewsTO obj = newsRepository.findByNewsHeaderAndNewsDate(newsTO.getNewsHeader(), newsTO.getNewsDate());
		if (obj == null) {
			return true;
		} else {
			newsTO = obj;
		}

		return false;
	}

	private boolean isEmailSent(NewsTO newsTO) {
		NewsTO obj = null;
		try {
			obj = newsRepository.findByIdNoticia(newsTO.getIdNoticia());
		} catch (Exception e) {
			// Encontrou inserido duas vezes.
			return true;
		}

		if (obj == null) {
			return false;
		} else {
			newsTO = obj;
		}

		if (obj.getEmailSent() == null || !obj.getEmailSent()) {
			return false;
		}

		return true;
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
		if (ticker.length() != 4) {
			int newIndex = newsHeader.indexOf("(", initialIndex + 5);
			ticker = newsHeader.substring(newIndex+1, newIndex + 5);
		}
		return ticker;
	}

	private String getQuotationsByPrefix(String prefix, Double income, Double vp) {
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
		Double lastQuote = getQuotation.getLastQuoteCache(ticker, true);
		if (lastQuote != null) {
			result.append(ticker);
			result.append(" : ");
			result.append("R$ " + numberFormat.format(lastQuote));
			result.append("\n");
		}

		if (vp != null) {
			result.append("VP: ");
			result.append("R$ " + numberFormat.format(vp));
			result.append("\n");
			
			if (lastQuote != null) {
				Double pvp = lastQuote / vp;
				result.append("P/VP: ");
				result.append(numberFormat.format(pvp));
				result.append("\n");
			}

		}

		if (income != null) {
			// History
			ArrayList<IncomeCompanyTO> list = new ArrayList<IncomeCompanyTO>();
			int count = -13;
			int avgCount = 0;
			Double avg = 0D;
			while (count <= 0) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, count);
				IncomeCompanyTO incomeCompany = incomeCompanyRepository.findByStockAndYearMonth(ticker, DateUtils.getYearMonth(cal.getTime()));
				if (incomeCompany != null) {
					avgCount++;
					list.add(incomeCompany);
				}
				count++;
			}
			StringBuffer hist = new StringBuffer();
			hist.append("\n Historico: \n");
			IncomeCompanyTO incomeBefore = null;
			String lastIncomeFormatted = "";
			for (IncomeCompanyTO incomeActual : list) {
				lastIncomeFormatted = "";
				avg += incomeActual.getValue();
				if (incomeBefore != null) {
					Double incBefore = incomeBefore.getValue();
					Double incActual = incomeActual.getValue();
					lastIncomeFormatted += (DateUtils.formatToMonthYear(incomeActual.getYearMonth()));
					lastIncomeFormatted += (": R$ " + numberFormatIncome.format(incActual));
					Double dy = (((incActual / incBefore) -1 ) * 100);
					lastIncomeFormatted += (" " + percentFormat.format(dy) + "%");
					lastIncomeFormatted += ("\n");
				} else {
					lastIncomeFormatted += (DateUtils.formatToMonthYear(incomeActual.getYearMonth()));
					lastIncomeFormatted += (": R$ " + numberFormatIncome.format(incomeActual.getValue()));
					lastIncomeFormatted += ("\n");
				}
				hist.append(lastIncomeFormatted);
				incomeBefore = incomeActual;
			}
			hist.append("\n");

			result.append(lastIncomeFormatted);
			result.append("\n");

			if (lastQuote != null) {
				result.append("DY: ");
				Double dy = (income / lastQuote) * 100;
				result.append(numberFormat.format(dy) + " % a.m.");
				result.append(" / " + numberFormat.format(dy * 12) + " % a.a.");
				result.append("\n");
				result.append("DY Médio: ");
				Double dyAvg = ((avg / avgCount) / lastQuote) * 100;
				result.append(numberFormat.format(dyAvg) + " % a.m.");
				result.append(" / " + numberFormat.format(dyAvg * 12) + " % a.a.");
				result.append("\n");
			}
			result.append(hist.toString());
		}

		result.append("\n");
		return result.toString();
	}

	/**
	 * Get This(Last) D.Y.
	 * @param income
	 * @param lastQuote
	 * @return
	 */
	public String getDYActual(Double income, Double lastQuote) {
		Double dy = (income / lastQuote) * 100;

		return numberFormat.format(dy);
	}

	/**
	 * Get AVG D.Y.
	 * @param ticker
	 * @param lastQuote
	 * @return
	 */
	public String getDYAvg(String ticker, Double lastQuote) {
		ArrayList<IncomeCompanyTO> list = new ArrayList<IncomeCompanyTO>();
		int count = -13;
		int avgCount = 0;
		Double avg = 0D;
		while (count <= 0) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, count);
			IncomeCompanyTO incomeCompany = incomeCompanyRepository.findByStockAndYearMonth(ticker, DateUtils.getYearMonth(cal.getTime()));
			if (incomeCompany != null) {
				avgCount++;
				list.add(incomeCompany);
			}
			count++;
		}
		StringBuffer hist = new StringBuffer();
		hist.append("\n Historico: \n");
		IncomeCompanyTO incomeBefore = null;
		String lastIncomeFormatted = "";
		for (IncomeCompanyTO incomeActual : list) {
			lastIncomeFormatted = "";
			avg += incomeActual.getValue();
			if (incomeBefore != null) {
				Double incBefore = incomeBefore.getValue();
				Double incActual = incomeActual.getValue();
				lastIncomeFormatted += (DateUtils.formatToMonthYear(incomeActual.getYearMonth()));
				lastIncomeFormatted += (": R$ " + numberFormatIncome.format(incActual));
				Double dy = (((incActual / incBefore) -1 ) * 100);
				lastIncomeFormatted += (" " + percentFormat.format(dy) + "%");
				lastIncomeFormatted += ("\n");
			} else {
				lastIncomeFormatted += (DateUtils.formatToMonthYear(incomeActual.getYearMonth()));
				lastIncomeFormatted += (": R$ " + numberFormatIncome.format(incomeActual.getValue()));
				lastIncomeFormatted += ("\n");
			}
			hist.append(lastIncomeFormatted);
			incomeBefore = incomeActual;
		}

		Double dyAvg = ((avg / avgCount) / lastQuote) * 100;
		return numberFormat.format(dyAvg);
	}

	public Double checkIncome(NewsTO newsTO) {
		Double income = null;
		if (newsTO.getNewsHeader().toLowerCase().contains("aviso aos cotistas")
				&& newsTO.getNewsHeader().toUpperCase().contains("(N)")) {

			newsTO.setNewLink(newsTO.getNewsHref());
			Document doc = this.getDocument(newsTO.getNewsHref());

			CompanyTO company = companyRepository.findByTicker(newsTO.getTicker() + "11");
			if (company == null) {
				company = companyRepository.findByTicker(newsTO.getTicker() + "11B");
			}

			if (doc == null) {
				income = 0D;	
			} else {
				IncomeHtmlParser htmlParser = new IncomeHtmlParser(doc);
				income = htmlParser.getValue();

				// Update company information
				company.setCnpj(htmlParser.getFundCnpj());
				company.setAdmName(htmlParser.getAdmName());
				company.setAdmCnpj(htmlParser.getAdmCnpj());
				company.setResponsible(htmlParser.getResponsible());
				company.setPhone(htmlParser.getPhone());
				company.setIsin(htmlParser.getIsin());
				companyRepository.save(company);
			}

			if (income != null) {
				IncomeCompanyTO incomeTO = new IncomeCompanyTO();
				Calendar cal = Calendar.getInstance();
				Date incomeDate;
				try {
					incomeDate = dateFormatNews.parse(newsTO.getNewsDate());
				} catch (ParseException e) {
					log.error(e);
					e.printStackTrace();
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
			String linkPdf = newsTO.getNewsHref();
			newsTO.setNewLink(newsTO.getNewsHref());

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

				try {
					File file = new File("/tmp/Anexo.html");
					Document doc = this.getDocument(linkPdf);
					if (doc != null) {
						FileUtils.write(file, doc.html(), "UTF-8");
						newsTO.setAttached(file);							
					}
				} catch (IOException e1) {
					log.error(e);
					e.printStackTrace();
				}
			}

		}

		return income;
	}

	private void getIncomesByNews(NewsTO newsBean) {
		String[] companies = newsBean.getNews().split("EMPRESA: ");
		StringBuffer buffer = new StringBuffer();
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

				buffer.append(getQuotationsByPrefix(ticker.substring(0, 4), incomeValue, cmp.getVp()));
				buffer.append("========================= \n");

				IncomeCompanyTO incomeTO = new IncomeCompanyTO();
				Calendar cal = Calendar.getInstance();
				Date incomeDate = null;
				Date paymentDate = null;
				try {
					incomeDate = dateFormat.parse(incomeDateStr);
					paymentDate = dateFormat.parse(paymentDateStr);
				} catch (ParseException e) {
					log.error(e);
					e.printStackTrace();
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
				log.error(e);
				e.printStackTrace();
				// Continue processing
			}
		}

		if (!buffer.toString().isEmpty()) {
			// Send Email
			SendMailSSL.send("Proventos Ex-" + dateFormat.format(new Date()), buffer.toString(), null, "dogaum@gmail.com");
		}

		newsBean.setEmailSent(true);
		newsRepository.save(newsBean);
	}

	public void checkIncomes(String sDate, String fDate) {

		String bodyItem = null;

		String url = "https://sistemasweb.b3.com.br/PlantaoNoticias/Noticias/ListarTitulosNoticias?agencia=18&palavra=proventos" + "&dataInicial=" + sDate + "&dataFinal=" + fDate;

		Document doc = this.getDocument(url);
		String result = doc.body().text();
		String headline = null;

		try {

			JSONArray arr = new JSONArray(result);
			String idNoticia = "";
			String dateTimeNoticia = "";
			for(int i = 0; i < arr.length(); i++) {
				idNoticia = arr.getJSONObject(i).getJSONObject("NwsMsg").getString("id");
				dateTimeNoticia = arr.getJSONObject(i).getJSONObject("NwsMsg").getString("dateTime");
				headline = arr.getJSONObject(i).getJSONObject("NwsMsg").getString("headline");
				String urlItem = String.format("https://sistemasweb.b3.com.br/PlantaoNoticias/Noticias/Detail?idNoticia=%s&agencia=18&dataNoticia=%s", idNoticia, dateTimeNoticia);
	
				Document docItem = this.getDocument(urlItem);;
				bodyItem = docItem.getElementById("conteudoDetalhe").getAllElements().text();

				NewsTO newsBean = new NewsTO();
				newsBean.setNewsHeader(headline);
				Date newsDate = dateFormatSearch.parse(dateTimeNoticia);
				newsBean.setNewsDate(dateFormatNewsSave.format(newsDate));
				newsBean.setStockType(getStockType(newsBean.getNewsHeader()));
				newsBean.setTicker(getStockTicker(newsBean.getNewsHeader()));
				newsBean.setNews(bodyItem);
				newsBean.setNewsHref(null);
				newsBean.setIdNoticia(idNoticia);

				if (insertNews(newsBean) && !isEmailSent(newsBean)) {
					this.getIncomesByNews(newsBean);
					break;
				}

			}
		} catch (Exception e ) {
			log.error("Erro ao pegar proventos.", e);
		}

	}
	
	public Double checkInformeMensal(NewsTO newsTO) {

		CompanyTO company = companyRepository.findByTicker(newsTO.getTicker() + "11");
		if (company == null) {
			company = companyRepository.findByTicker(newsTO.getTicker() + "11B");
		}

		if (company == null) {
			return 0D;
		}

		if (newsTO.getNewsHeader().toLowerCase().contains("informe mensal")
				&& !newsTO.getNewsHeader().toLowerCase().contains("(C)")) {

			newsTO.setNewLink(newsTO.getNewsHref());
			Document doc = this.getDocument(newsTO.getNewsHref());
			if (doc == null) {
				return company.getVp();
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

			InformeMensalTO old = null;
			if (newsTO.getNewsHeader().toLowerCase().contains("(R)")) {
				old = informeMensalRepository.findByTickerAndMesCompetencia(company.getTicker(), htmlParser.getMesCompetencia());
				informeMensalRepository.deleteById(old.getId());
			}

			company.setQtdCotas(htmlParser.getQtdCotas());
			company.setQtdCotistas(htmlParser.getQtdCotistas());
			company.setAtivo(htmlParser.getAtivo());
			company.setVp(htmlParser.getVp());
			company.setTotalDisponibilidade(htmlParser.getTotalDisponibilidade());

			if (company.getLastUpdate() == null || company.getLastUpdate().before(htmlParser.getInfoDate())) {
				company.setLastUpdate(htmlParser.getInfoDate());
				companyRepository.save(company);
			}

			InformeMensalTO informeMensalTO = htmlParser.getObject();
			informeMensalTO.setTicker(company.getTicker());
			informeMensalRepository.save(informeMensalTO);
			
			return htmlParser.getVp();
		}

		return company.getVp();
	}

	public int runInformeMensal(String query, NewsFilterType ft, String sDate, String fDate, int pages) {

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
				Document doc = this.getDocument(finalUrl);
				Element pagina = doc.getElementById("linksNoticias");
				if (pagina != null) {
					Elements links = pagina.getElementsByTag("li");
					int el = 0;
					for (Element link : links) {
						el++;
						System.out.println("Página " + i + "/" + pages + ". " + el + "/"+ links.size() + " Links");
						try {
							String href = link.getElementsByTag("a").get(0).attr("href");
							String newsName = link.text();

							doc = this.getDocument(prefix + href);
							Element news = doc.getElementById("contentNoticia");
							while (news == null) {
								Thread.sleep(10 * 1000);
								doc = this.getDocument(prefix + href);
								news = doc.getElementById("contentNoticia");
							}

							NewsTO newsBean = new NewsTO();
							newsBean.setNewsHeader(newsName.substring(19));
							//TODO newsBean.setNewsDate(getDateFromNews(newsName));
							newsBean.setStockType(getStockType(newsBean.getNewsHeader()));
							newsBean.setTicker(getStockTicker(newsBean.getNewsHeader()));
							newsBean.setNews(news.text());
							newsBean.setNewsHref(prefix + href);

							if (newsBean.getNewsHeader().toLowerCase().contains("fii")) {
								qtyNews++;

								try {
									checkInformeMensal(newsBean);	
								} catch (Exception e) {
									log.error("Erro ao verificar o Informe mensal. " + newsName);
									log.error(e);
									e.printStackTrace();
								}

							}
						} catch (Exception e) {
							log.error(e);
							e.printStackTrace();
						}

					}
				}

			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}
		}
		
		return qtyNews;
	}
}