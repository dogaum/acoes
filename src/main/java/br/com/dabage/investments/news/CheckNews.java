package br.com.dabage.investments.news;

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
		String prefix = "http://www.bmfbovespa.com.br/Agencia-Noticias/";

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2014);
		cal.set(Calendar.MONTH, 3);

		while(cal.getTime().before(new Date())) {
			String endFII = "http://www.bmfbovespa.com.br/Agencia-Noticias/ListarNoticias.aspx?idioma=pt-br&q=rendimento&tipoFiltro=3&periodoDe=INICIO&periodoAte=FIM&pg=";

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

							checkIncome(newsBean);
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

		String endFII = "http://www.bmfbovespa.com.br/Agencia-Noticias/ListarNoticias.aspx?idioma=pt-br&q=" + query + "&tipoFiltro=0";
		String prefix = "http://www.bmfbovespa.com.br/Agencia-Noticias/";

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

					doc = Jsoup.connect(prefix + href).get();
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
						SendMailSSL.send(newsName, getQuotationsByPrefix(newsBean.getTicker(), income) + news.text());
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
		Double amortization = null;
		if (newsTO.getNewsHeader().toUpperCase().contains("RENDIMENTO")) {

			String[] array = newsTO.getNews().toUpperCase().split("\n");
			for (int i = 0; i < array.length; i++) {
				String linha = array[i];
				if (linha.contains("R$") && !linha.contains("AMORTIZA")) {
					income = getIncome(linha);
				} else if (linha.contains("R$") && linha.contains("AMORTIZA")) {
					amortization = getIncome(linha);
				}
			}

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


/*				if (company.getIncomes() == null) {
					company.setIncomes(new ArrayList<IncomeCompanyTO>());
				}

				company.getIncomes().add(incomeTO);

				companyRepository.save(company);*/
			}
		}
		return income;
	}
}