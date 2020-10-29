package br.com.dabage.investments.news;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import br.com.dabage.investments.carteira.CarteiraTO;
import br.com.dabage.investments.carteira.IncomeTO;
import br.com.dabage.investments.carteira.IncomeTypes;
import br.com.dabage.investments.carteira.NegotiationTO;
import br.com.dabage.investments.carteira.PortfolioItemTO;
import br.com.dabage.investments.company.CompanyTO;
import br.com.dabage.investments.company.IncomeCompanyTO;
import br.com.dabage.investments.company.InsertFIITickers;
import br.com.dabage.investments.company.InsertTickers;
import br.com.dabage.investments.config.ConfigService;
import br.com.dabage.investments.config.StockTypeTO;
import br.com.dabage.investments.mail.SendMailSSL;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.repositories.CarteiraRepository;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.IncomeCompanyRepository;
import br.com.dabage.investments.repositories.IncomeRepository;
import br.com.dabage.investments.repositories.NegotiationRepository;
import br.com.dabage.investments.repositories.NewsRepository;
import br.com.dabage.investments.repositories.PortfolioItemRepository;
import br.com.dabage.investments.repositories.RoleRepository;
import br.com.dabage.investments.repositories.StockTypeRepository;
import br.com.dabage.investments.repositories.UserRepository;
import br.com.dabage.investments.user.UserTO;
import br.com.dabage.investments.utils.DateUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(inheritLocations=true, locations = { "classpath:mongo-config.xml", "classpath:mvc-dispatcher-servlet.xml" })
public class CheckNewsTest {

	static DecimalFormatSymbols symbols = null;

	static {
		symbols = new DecimalFormatSymbols (new Locale ("pt", "BR"));
		symbols.setMinusSign('-');
		symbols.setPercent('%');
	}

	static DateFormat dateFormatSearch = new SimpleDateFormat("yyyy-MM-dd");

	static NumberFormat numberFormatIncome = new DecimalFormat ("#,##0.000000", symbols);

	static DecimalFormat numberFormat = new DecimalFormat ("#,##0.00", symbols);

	@Autowired
	public CheckNews checkNews;

	@Autowired
	public CheckNewsB3 checkNewsB3;
	
	@Autowired
	private IncomeCompanyRepository incomeCompanyRepository;

	@Autowired
	private ConfigService configService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

	@Autowired
	public InsertFIITickers insertFIITickers;

	@Autowired
	public InsertTickers insertTickers;
	
	@Autowired
	private GetQuotation getQuotation;

	@Autowired
	private NewsRepository newsRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private NegotiationRepository negotiationRepository;

	@Autowired
	private IncomeRepository incomeRepository;

	@Autowired
	private PortfolioItemRepository portfolioItemRepository;

	@Autowired
	CarteiraRepository carteiraRepository;

	@Autowired
	StockTypeRepository stockTypeRepository;

	@Test
	public void testRun() {
		String query = "fii";
		String sDate = "2018-10-26";
		String fDate = "2018-10-26";
		int qtyNews = checkNews.run(query, NewsFilterType.INTERVAL, sDate, fDate, 2);
		System.out.println(qtyNews);
	}

	@Test
	public void testCheckIncome() {
		String query = "aviso aos cotistas";
		String sDate = "2018-06-29";
		String fDate = "2018-06-29";
		int qtyNews = checkNews.run(query, NewsFilterType.INTERVAL, sDate, fDate, 10);
		System.out.println(qtyNews);
	}

	@Test
	public void testCheckInformeMensal() {
		String query = "informe mensal";
		String sDate = "2019-07-01";
		String fDate = "2019-08-31";
		checkNews.runInformeMensal(query, NewsFilterType.INTERVAL, sDate, fDate, 12);
	}
	
	@Test
	public void testCheckIncomes() {
		String ticker = "BRCR11";
		StringBuffer result = new StringBuffer();

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
		numberFormat.setPositivePrefix("+");
		IncomeCompanyTO incomeBefore = null;
		for (IncomeCompanyTO incomeActual : list) {
			if (incomeBefore != null) {
				Double incBefore = incomeBefore.getValue();
				Double incActual = incomeActual.getValue();
				result.append("Rendimento " + DateUtils.formatToMonthYear(incomeActual.getYearMonth()));
				result.append(": R$ " + numberFormatIncome.format(incActual));
				Double dy = (((incActual / incBefore) -1 ) * 100);
				result.append(" " + numberFormat.format(dy) + "%");
				result.append("\n");
			} else {
				result.append("Rendimento " + DateUtils.formatToMonthYear(incomeActual.getYearMonth()));
				result.append(": R$ " + numberFormatIncome.format(incomeActual.getValue()));
				result.append("\n");
			}
			incomeBefore = incomeActual;
		}

		System.out.println(result.toString());
		
	}

	@Test
	public void testCheckIncomesTemp() {
		// get attachment
		String linkPdf = null;
		String link = "https://fnet.bmfbovespa.com.br/fnet/publico/visualizarDocumento?id=18132";
		if (link.contains("https")) {
			File file = null;
			linkPdf = link.replaceFirst("https", "http");
			linkPdf = linkPdf.replaceAll("visualizarDocumento", "exibirDocumento");

			PdfReader reader;
			try {
				reader = new PdfReader(linkPdf);
				String dados = PdfTextExtractor.getTextFromPage(reader, reader.getNumberOfPages());
				IncomePdfParser parser = new IncomePdfParser(dados);
				URL url = new URL(linkPdf);
				file = new File("anexo.pdf");
				FileUtils.copyURLToFile(url, file);
				reader.close();
			} catch (IOException e) {
				// Não é PDF, é HTML
				// Pegar o html e enviar por email
				Connection connection = Jsoup.connect(linkPdf);
				connection.ignoreHttpErrors(true);
				connection.timeout(30000);

				try {
					file = new File("Anexo.html");
					Document doc = connection.get();
					FileUtils.write(file, doc.html(), "UTF-8");
				} catch (IOException e1) {
				}
			}

			SendMailSSL.send(
					"Teste",
					"Teste",
					file,
					"dogaum@gmail.com");
		}
	}

	@Test
	public void testCheckIncomesByInterval() {
		Calendar initialDate = Calendar.getInstance();
		initialDate.add(Calendar.DAY_OF_MONTH, -1);
		checkNews.checkIncomesByInterval(initialDate.getTime(), initialDate.getTime());
	}

	private int run(String query, NewsFilterType ft, String sDate, String fDate) {

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

		try {
			Connection connection = Jsoup.connect(endFII.toString());
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
					newsBean.setNews(news.text());
					newsBean.setNewsHref(prefix + href);

					if (newsBean.getNewsHeader().toLowerCase().contains("aviso aos cotistas")) {
						int indexIni = newsBean.getNews().indexOf("http");
						int indexFin = newsBean.getNews().lastIndexOf("flnk");
						String linkPdf = newsBean.getNews().substring(indexIni, indexFin-1);
						linkPdf = linkPdf.replaceFirst("https", "http");
						linkPdf = linkPdf.replaceAll("visualizarDocumento", "exibirDocumento");
						
						newsBean.setNewLink(linkPdf);

						PdfReader reader;
						IncomePdfParser parser = null;
						try {
							reader = new PdfReader(linkPdf);
							String dados = PdfTextExtractor.getTextFromPage(reader, 1);
							URL url = new URL(linkPdf);
							File file = new File("/tmp/Anexo.pdf");
							FileUtils.copyURLToFile(url, file);
							newsBean.setAttached(file);
							parser = new IncomePdfParser(dados);
							reader.close();
						} catch (IOException e) {
							// Não é PDF, é HTML
							// Pegar o html e enviar por email
							connection = Jsoup.connect(linkPdf);
							connection.ignoreHttpErrors(true);
							connection.timeout(30000);

							try {
								File file = new File("/tmp/Anexo.html");
								doc = connection.get();
								IncomeHtmlParser parseHtml = new IncomeHtmlParser(doc);
								FileUtils.write(file, doc.html(), "UTF-8");
								newsBean.setAttached(file);
							} catch (IOException e1) {
							}

						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return qtyNews;
	}

	@Test
	public void testPortfolioIR() {
		configService.calculatePortfolioIR(2017);
	}

	@Test
	public void testCalculatePortfolio() {
		configService.calcPortfolioItem();
	}

	@Test
	public void testInsertNewCompanies() {
		insertFIITickers.run();
		//insertTickers.run();
	}

	@Test
	public void testLastNegotiation() {
		CarteiraTO carteira = carteiraRepository.findByName("FII");
		NegotiationTO neg = negotiationRepository.findTopByIdCarteiraOrderByDtNegotiationDesc(carteira.getId());
		System.out.println(neg.getStock() + " - " + neg.getDtNegotiation());

		IncomeTO inc = incomeRepository.findTopByIdCarteiraOrderByIncomeDateDesc(carteira.getId());
		System.out.println(inc.getStock() + " - " + inc.getIncomeDate());
	}

	@Test
	public void insertManualFII() {
		StockTypeTO stockType = stockTypeRepository.findByName("FII");
		
		String ticker = "RBRS11";
		String name = "RIO BRAVO RENDA RESIDENCIAL";
		String fullName = "RIO BRAVO RENDA RESIDENCIAL";
		String setor = "Residencial"; //Logístico Escritório Hotéis Varejo Recebíveis Fundos Educacional Desenvolvimento

		CompanyTO company = new CompanyTO(ticker, name, fullName);
		company.setStockType(stockType);
		company.setCategory("FII");
		company.setSetor(setor);
		companyRepository.save(company);	
	}

	@Test
	public void insertManualStock() {
		StockTypeTO stockType = stockTypeRepository.findByName("ACOES");
		
		String ticker = "DMVF3";
		String name = "D1000 VAREJO FARMA PARTICIPAÇÕES S.A.";
		String fullName = "D1000 VAREJO FARMA PARTICIPAÇÕES S.A.";
		String setor = "Farmacêutico"; //Logístico Escritório Hotéis Varejo Recebíveis Fundos Educacional Desenvolvimento

		CompanyTO company = new CompanyTO(ticker, name, fullName);
		company.setStockType(stockType);
		company.setCategory("Ações");
		company.setSetor(setor);
		companyRepository.save(company);	
	}
	
	@Test
	public void testGetIdNoticia() {
		String header = "http://www2.bmfbovespa.com.br/Agencia-Noticias/ListarNoticias.aspx?idioma=pt-br&idNoticia=760407&header=201810290905PROVENTOS+DOS+EMISSORES+COTADOS+NA+FORMA+%5cEX%5c%3a+29%2f10%2f2018760407&tk=02491c76bfb3aab4ef1522f9137adb91&WT.ac=PROVENTOS+DOS+EMISSORES+COTADOS+NA+FORMA+%5cEX%5c%3a+29%2f10%2f2018";
		String idNoticia = "";
		int initialIndex = header.indexOf("idNoticia=");
		int finalIndex = header.indexOf("&header");
		idNoticia = header.substring(initialIndex +10, finalIndex);

		System.out.println(idNoticia);
	}

	@Test
	@Rollback(false)
	public void testRun2() {
		UserTO user = userRepository.findByEmail("dogaum@gmail.com");
		System.out.println(user);
	}

	@Test
	public void testIncomeCompaniesByDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, 9);
		cal.set(Calendar.DAY_OF_MONTH, 31);

		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		System.out.println(DateUtils.formatDateToStr(cal.getTime()));

		//Collection<IncomeCompanyTO> incomes = incomeCompanyRepository.findByIncomeDate(cal.getTime());
		Collection<IncomeCompanyTO> incomes = incomeCompanyRepository.findByYearMonth(201810);
		System.out.println("Encontrados: " + incomes.size());
		for (IncomeCompanyTO icTO : incomes) {
			String ticker = icTO.getStock();
			Double income = icTO.getValue();

			String paymentDate = "";
			if (icTO.getPaymentDate() != null) {
				paymentDate = DateUtils.formatDateToStr(icTO.getPaymentDate());
			}

			String announcementDate = "";
			if (icTO.getIncomeDate() != null) {
				announcementDate = DateUtils.formatDateToStr(icTO.getIncomeDate());
			}

			String quote = "";
			Double lastQuote = getQuotation.getLastQuoteCache(ticker, false);
			if (lastQuote != null && lastQuote > 0) {
				quote = numberFormat.format(lastQuote);
			}

			String dyActual = "";
			String dyAVG = "";
			if (income != null && income > 0
					&& lastQuote != null && lastQuote > 0) {
				dyActual = checkNews.getDYActual(income, lastQuote);
				dyAVG = checkNews.getDYAvg(ticker, lastQuote);
			}

			System.out.print(ticker);
			System.out.print("	");
			System.out.print(numberFormatIncome.format(income));
			System.out.print("	");
			System.out.print(dyActual);
			System.out.print("	");
			System.out.print(dyAVG);
			System.out.print("	");
			System.out.print(quote);
			System.out.print("		");
			System.out.print(announcementDate);
			System.out.print("	");
			System.out.print(paymentDate);
			System.out.println("	");
		}
	}

	@Test
	public void agruparAtivos() {
		String ticker = "HBOR3";
		int multi = 10;

		// Altera os rendimentos
		List<IncomeCompanyTO> incomes = incomeCompanyRepository.findByStockOrderByIncomeDateDesc(ticker);
		for (IncomeCompanyTO income : incomes) {
			Double oldValue = income.getValue();
			income.setValue(income.getValue() * multi);
			incomeCompanyRepository.save(income);
			System.out.println(income.getYearMonth() + " : " + oldValue + "  >> " + income.getValue());
		}

		// Altera a posição
		List<NegotiationTO> negotiations = negotiationRepository.findByStockOrderByDtNegotiationAsc(ticker);
		for (NegotiationTO negotiationTO : negotiations) {
			//TODO
		}
	}

	@Test
	public void splitAtivos() {
		String ticker = "FCFL11";
		int qty = 10;

		List<IncomeCompanyTO> incomes = incomeCompanyRepository.findByStockOrderByIncomeDateDesc(ticker);
		for (IncomeCompanyTO income : incomes) {
			//System.out.println(income.getYearMonth() + " " + income.getValue());
			Double oldValue = income.getValue();
			if (income.getValue() > 1D) {
				income.setValue(income.getValue() / qty);
				incomeCompanyRepository.save(income);
				System.out.println(income.getYearMonth() + " : " + oldValue + "  >> " + income.getValue());
			}
		}
	}

	@Test
	public void alterarTicker() {
		String de = "UBSR11";
		String para = "RECR11";
		String newName = "FII REC RECE";

		String prefixDe = de.substring(0, 4);
		String prefixPara = para.substring(0, 4);

		// News
		List<NewsTO> news = newsRepository.findByTickerIgnoreCase(prefixDe);
		for (NewsTO newsTO : news) {
			newsTO.setTicker(prefixPara);
			newsRepository.save(newsTO);
		}

		System.out.println("NewsTO alterado.");
		
		// Company
		CompanyTO company = companyRepository.findByPrefix(prefixDe).get(0);
		company.setPrefix(prefixPara);
		company.setTicker(para);
		if (newName != null && !newName.isEmpty()) {
			company.setName(newName);	
		}

		company = companyRepository.save(company);

		System.out.println("CompanyTO alterado.");
		
		// IncomeCompany
		List<IncomeCompanyTO> incomesCompany = incomeCompanyRepository.findByIdCompany(company.getId());
		for (IncomeCompanyTO incomeCompanyTO : incomesCompany) {
			incomeCompanyTO.setStock(para);
			incomeCompanyRepository.save(incomeCompanyTO);
		}

		System.out.println("IncomeCompanyTO alterado.");
		
		//Negotiations
		List<NegotiationTO> negotiations = negotiationRepository.findByStockOrderByDtNegotiationAsc(de);
		if (negotiations != null) {
			for (NegotiationTO negotiationTO : negotiations) {
				negotiationTO.setStock(para);
				negotiationRepository.save(negotiationTO);
			}
		}

		System.out.println("NegotiationTO alterado.");

		// Incomes
		List<IncomeTO> incomes = incomeRepository.findByStockOrderByIncomeDateAsc(de);
		if (incomes != null) {
			for (IncomeTO incomeTO : incomes) {
				incomeTO.setStock(para);
				incomeRepository.save(incomeTO);
			}
		}

		System.out.println("IncomeTO alterado.");

		// Portifolio Item
		List<PortfolioItemTO> portfolioItems = portfolioItemRepository.findByStock(de);
		if (portfolioItems != null) {
			for (PortfolioItemTO portfolioItemTO : portfolioItems) {
				portfolioItemTO.setStock(para);
				portfolioItemRepository.save(portfolioItemTO);
			}
		}
		System.out.println("PortfolioItemTO alterado.");
	}

	@Test
	public void testCheckNewsB3() {
		String query = "fii";
		String sDate = "2019-12-04";
		String fDate = "2019-12-04";
		checkNewsB3.run(query, sDate, fDate);

	}

	@Test
	public void testCheckIncomesB3() {

		String sDate = "2020-01-02";
		String fDate = "2020-01-02";
		checkNewsB3.checkIncomes(sDate, fDate);

	}

	@Test
	public void rendimentosPorAno() {
		int ano = 2019;
		CarteiraTO carteira = carteiraRepository.findByName("FII");
		List<IncomeTO> incomes = incomeRepository.findByIdCarteiraAndType(carteira.getId(), IncomeTypes.INCOME);
		Map<String, Double> mapa = new HashMap<String, Double>();
		for (IncomeTO incomeTO : incomes) {
			if (incomeTO.getIncomeDate().getYear() != (ano - 1900)) continue;
			if (mapa.containsKey(incomeTO.getStock())) {
				Double value = mapa.get(incomeTO.getStock());
				mapa.put(incomeTO.getStock(), (value + incomeTO.getValue()));
			} else {
				mapa.put(incomeTO.getStock(), incomeTO.getValue());	
			}

		}

		System.out.println("Fundo;Rendimento");
		Iterator<String> it = mapa.keySet().iterator();
		while (it.hasNext()) {
			String stock = (String) it.next();
			Double value = mapa.get(stock);
			System.out.println(stock + ";" + numberFormat.format(value));
		}
	}

}
