package br.com.dabage.investments.news;

import static org.junit.Assert.fail;

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
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.com.dabage.investments.company.IncomeCompanyTO;
import br.com.dabage.investments.config.ConfigService;
import br.com.dabage.investments.mail.SendMailSSL;
import br.com.dabage.investments.repositories.IncomeCompanyRepository;
import br.com.dabage.investments.utils.DateUtils;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

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

	@Resource
	private IncomeCompanyRepository incomeCompanyRepository;

	@Autowired
	private ConfigService configService;
	
	@Test
	public void testRun2() {
		fail("Not yet implemented");
	}

	@Test
	public void testRun() {
		String query = "fii";
		int qtyNews = checkNews.run(query, NewsFilterType.DAY, null, null, 1);
		System.out.println(qtyNews);
	}

	@Test
	public void testCheckIncome() {
		String query = "aviso aos cotistas";
		String sDate = "2017-11-03";
		String fDate = "2017-11-03";
		int qtyNews = checkNews.run(query, NewsFilterType.INTERVAL, sDate, fDate, 1);
		System.out.println(qtyNews);
	}

	@Test
	public void testCheckInformeMensal() {
		String query = "informe mensal";
		String sDate = "2018-02-01";
		String fDate = "2018-02-18";
		checkNews.run(query, NewsFilterType.INTERVAL, sDate, fDate, 9);
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
		fail("Not yet implemented");
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
}
