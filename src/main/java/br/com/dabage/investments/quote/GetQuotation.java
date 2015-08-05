package br.com.dabage.investments.quote;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class GetQuotation {

	static String cmaQuoteUrl = "http://acoes.agronegocios-e.com.br/Grafico//CmaGraf.php?O=12&P=";

	static SimpleDateFormat preFormatDate = new SimpleDateFormat("yyMMdd");
	static SimpleDateFormat unFormatDate = new SimpleDateFormat("dd/MM");
	static SimpleDateFormat unFormatCompleteDate = new SimpleDateFormat("yyyy-MM-dd");
	static NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
	static NumberFormat unFormatNumber = NumberFormat.getInstance();

	static Map<String, Quote> quoteCache;

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
				e.printStackTrace();
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
			e.printStackTrace();
		}
		
		return "";
	}

	public Double getLastQuote(String stock) {
		
		try {
			Document doc = Jsoup.connect(cmaQuoteUrl + stock).get();
			String[] lines = doc.body().text().split(" ");
			if (lines.length < 2) {
				return 0D;
			}
			String lastLine = lines[lines.length - 1];
			String[] lineValues = lastLine.split(",");

			return new Double(lineValues[4]);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0D;
	}

	public Double getLastQuoteCache(String stock) {
		if (quoteCache == null) {
			quoteCache = new HashMap<String, Quote>();
		}

		Quote quote = null;

		if (quoteCache.containsKey(stock)) {
			quote = quoteCache.get(stock);
		}

		if (quote == null || (quote.getLastUpdate().getTime() + 15*60*1000) < new Date().getTime()) {
			try {
				Document doc = Jsoup.connect(cmaQuoteUrl + stock).get();
				String[] lines = doc.body().text().split(" ");
				if (lines.length < 2) {
					return 0D;
				}
				String lastLine = lines[lines.length - 1];
				String[] lineValues = lastLine.split(",");

				Date date = preFormatDate.parse(lineValues[0]); //Date;
				quote = new Quote();
				quote.setDate(date);
				quote.setLastUpdate(new Date());

				quote.setOpen(Double.valueOf(lineValues[1])); //Opean value
				quote.setLow(Double.valueOf(lineValues[3])); //Min value
				quote.setHigh(Double.valueOf(lineValues[2])); //Max value
				quote.setClose(Double.valueOf(lineValues[4])); //Last value 
				quote.setStock(stock);

				quoteCache.put(stock, quote);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}			
		}

		if (quote == null) {
			return 0D;
		} else {
			return quote.getClose();
		}
	}

	/**
	 * Refresh the Quote Cache
	 */
	public void refreshQuoteCache() {
		quoteCache = new HashMap<String, Quote>();
	}

	public JSONObject getJsonData(String stock) {

		try {
			Document doc = Jsoup.connect(cmaQuoteUrl + stock).get();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public List<Quote> getQuoteHist(String stock) {

		List<Quote> result = new ArrayList<Quote>();
		
		try {
			Document doc = Jsoup.connect(cmaQuoteUrl + stock).get();
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

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private String getNumberFormat(String usNumberFormat) {
		Number number = null;
		try {
			number = numberFormat.parse(usNumberFormat);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return unFormatNumber.format(number);
	}

}
