package br.com.dabage.investments.quote;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.utils.DateUtils;

@Component
public class GetQuotation {

	private Logger log = LogManager.getLogger(GetQuotation.class);

	static String cmaQuoteUrl = "http://acoes.agronegocios-e.com.br/Grafico//CmaGraf.php?O=12&P=";

	static String googleQuoteUrl = "https://finance.google.com/finance/getprices?q=";

	static String itauQuoteUrl = "http://www.itaucorretora.com.br/Finder/Finder/?stock=";
	
	static SimpleDateFormat preFormatDate = new SimpleDateFormat("yyMMdd");
	static SimpleDateFormat unFormatDate = new SimpleDateFormat("dd/MM");
	static SimpleDateFormat unFormatCompleteDate = new SimpleDateFormat("yyyy-MM-dd");
	static NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
	static NumberFormat unFormatNumber = NumberFormat.getInstance();

	static Map<String, Quote> quoteCache;

	static final int TIMEOUT = 2500;

	public String getLastQuotation(String stock) {
	
		try {
			Document doc = Jsoup.connect(cmaQuoteUrl + stock).get();
			String[] lines = doc.body().text().split(" ");
			if (lines.length < 2) {
				return "";
			}
			String lastLine = lines[lines.length - 1];
			String[] lineValues = lastLine.split(",");

			Date date = null;
			try {
				date = preFormatDate.parse(lineValues[0]);
			} catch (ParseException e) {
				log.error(e);
			} // Data;
			String data = unFormatCompleteDate.format(date);

			JSONObject json = new JSONObject();
			try {
				json.put("data", data);
				json.put("abertura", getNumberFormat(lineValues[1]));
				json.put("minima", getNumberFormat(lineValues[2]));
				json.put("maxima", getNumberFormat(lineValues[3]));
				json.put("ultima", getNumberFormat(lineValues[4]));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return json.toString();
		} catch (IOException e) {
			log.error(e);
		}
		
		return "";
	}

	public void renewCache() {
		if (quoteCache != null) {
			for(Quote quote : quoteCache.values()) {
				if (isCacheInvalidate(quote.getLastUpdate())) {
					//getQuoteForCache(quote.getStock(), quote);
					getQuoteFromItau(quote.getStock(), quote);
				}
			}
		}
	}
	
	private boolean isCacheInvalidate(Date lastUpdate) {
		Calendar cal = Calendar.getInstance();
		if (DateUtils.isWorkingDay(cal) && isWorkHour(cal)
				&& (lastUpdate.getTime() + 13*60*1000) < cal.getTimeInMillis()) {
			return true;
		}
		return false;
	}

	private static boolean isWorkHour(Calendar cal) {
		Calendar calIni = Calendar.getInstance();
		calIni.set(Calendar.HOUR_OF_DAY, 9);
		calIni.set(Calendar.MINUTE, 0);

		Calendar calEnd = Calendar.getInstance();
		calEnd.set(Calendar.HOUR_OF_DAY, 18);
		calEnd.set(Calendar.MINUTE, 30);

		if (cal.before(calIni) || cal.after(calEnd)) {
			return false;
		}

		return true;
	}
	
	public Double getLastQuoteCache(String stock, boolean renew) {
		if (quoteCache == null) {
			quoteCache = new HashMap<String, Quote>();
		}

		Quote quote = null;

		if (quoteCache.containsKey(stock)) {
			quote = quoteCache.get(stock);
		}

		if (quote == null || isCacheInvalidate(quote.getLastUpdate()) || renew) {
			getQuoteFromItau(stock, quote);
			quote = quoteCache.get(stock);
		}

		if (quote == null) {
			return 0D;
		} else {
			return quote.getClose();
		}
	}

	private void getQuoteFromItau(String stock, Quote quote) {
		try {
			Connection connection = Jsoup.connect(itauQuoteUrl + stock);
			connection.ignoreHttpErrors(true);
			connection.timeout(TIMEOUT);

			Document doc = connection.get();
			Elements classes = doc.getElementsByClass("preco");
			String preco = classes.get(0).getElementsByTag("strong").text().trim();
			preco = preco.replace(".", "");
			preco = preco.replace(",", ".");
			Double lastQuote = Double.valueOf(preco);

			if (quote == null) quote = new Quote();
			quote.setDate(new Date());
			quote.setLastUpdate(new Date());

			quote.setOpen(lastQuote); //Opean value
			quote.setLow(lastQuote); //Min value
			quote.setHigh(lastQuote); //Max value
			quote.setClose(lastQuote); //Last value 
			quote.setStock(stock);

			quoteCache.put(stock, quote);

		} catch (Exception e) {
			if (quote == null) quote = new Quote();
			quote.setDate(new Date());
			quote.setLastUpdate(new Date());

			quote.setOpen(0); //Opean value
			quote.setLow(0); //Min value
			quote.setHigh(0); //Max value
			quote.setClose(0); //Last value 
			quote.setStock(stock);

			quoteCache.put(stock, quote);
			log.error("Erro ao pegar cotação do Itau. Stock: " + stock);
		}

	}

	/**
	 * Refresh the Quote Cache
	 */
	public void cleanQuoteCache() {
		quoteCache = new HashMap<String, Quote>();
	}

	public JSONObject getJsonData(String stock) {

		try {
			Connection connection = Jsoup.connect(cmaQuoteUrl + stock);
			connection.ignoreHttpErrors(true);
			connection.timeout(TIMEOUT);
			Document doc = connection.get();
			String[] lines = doc.body().text().split(" ");
			if (lines.length < 2) {
				return null;
			}

			Collection<Double> data = new ArrayList<Double>();
			Collection<Double> dataMin = new ArrayList<Double>();;
			Collection<Double> dataMax = new ArrayList<Double>();;
			Collection<String> dates = new ArrayList<String>();

			int idx = 0;
			for (int i = 0; i < lines.length; i++) {
				String[] lineValues = lines[i].split(",");
				if (lineValues.length < 6) {
					idx++;
				} else {
					break;
				}
			}

			int div = (lines.length - idx) / 20;
			for (int i = idx; i < lines.length; i++) {
				String[] lineValues = lines[i].split(",");
				if (i == idx || i % div == 0 || i == lines.length - 1) {
					Date date = preFormatDate.parse(lineValues[0]); //Date;
					dates.add(unFormatDate.format(date)); //Date					
				}

				dataMin.add(Double.valueOf(lineValues[2])); //Min value
				dataMax.add(Double.valueOf(lineValues[3])); //Max value
				data.add(Double.valueOf(lineValues[4])); //Last value
			}

			JSONObject obj = new JSONObject();
			JSONArray dataArray = new JSONArray(data);
			JSONArray dataMinArray = new JSONArray(dataMin);
			JSONArray dataMaxArray = new JSONArray(dataMax);
			JSONArray datesArray = new JSONArray(dates);
			try {
				obj.put("data", dataArray);
				obj.put("dataMin", dataMinArray);
				obj.put("dataMax", dataMaxArray);
				obj.put("labels", datesArray);
				return obj;
			} catch (JSONException e) {
				log.error(e);
			}
		} catch (IOException | ParseException e) {
			log.error(e);
		}
		
		return null;
	}

	public List<Quote> getQuoteHist(String stock) {

		List<Quote> result = new ArrayList<Quote>();
		
		try {
			Connection connection = Jsoup.connect(cmaQuoteUrl + stock);
			connection.ignoreHttpErrors(true);
			connection.timeout(TIMEOUT);
			Document doc = connection.get();
			String[] lines = doc.body().text().split(" ");
			if (lines.length < 2) {
				return result;
			}

			Quote quote = null;
			int idx = 0;
			for (int i = 0; i < lines.length; i++) {
				String[] lineValues = lines[i].split(",");
				if (lineValues.length < 6) {
					idx++;
				} else {
					break;
				}
			}

			for (int i = idx; i < lines.length; i++) {
				quote = new Quote();
				String[] lineValues = lines[i].split(",");
				Date date = preFormatDate.parse(lineValues[0]); //Date;
				quote.setDate(date);					

				quote.setOpen(Double.valueOf(lineValues[1])); //Opean value
				quote.setLow(Double.valueOf(lineValues[3])); //Min value
				quote.setHigh(Double.valueOf(lineValues[2])); //Max value
				quote.setClose(Double.valueOf(lineValues[4])); //Last value 
				quote.setStock(stock);

				result.add(quote);
			}

		} catch (IOException | ParseException e) {
			log.error(e);
		}
		
		return result;
	}
	
	private String getNumberFormat(String usNumberFormat) {
		Number number = null;
		try {
			number = numberFormat.parse(usNumberFormat);
		} catch (ParseException e) {
			log.error(e);
		}
		return unFormatNumber.format(number);
	}

}
