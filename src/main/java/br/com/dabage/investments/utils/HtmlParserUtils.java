package br.com.dabage.investments.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class HtmlParserUtils {

	static NumberFormat numberFormat = new DecimalFormat ("#,##0.00", new DecimalFormatSymbols (new Locale ("pt", "BR")));

	public static String getValue(Document doc, int tableX, int trX, int tdX) {
		String result = "";
		Elements tables = doc.getElementsByTag("table");
		Element table = tables.get(tableX -1);
		Elements trs = table.getElementsByTag("tr");
		Element tr = trs.get(trX -1);
		Elements tds = tr.getElementsByTag("td");
		Element td = tds.get(tdX -1);

		result = td.text();
		if (result != null && result.isEmpty()) {
			result = null;
		}
		return result;
	}

	public static Long parseLong(String str) {
		Long result = null;
		if (str == null || str.isEmpty()) {
			return result;
		}

		try {
			result = numberFormat.parse(str).longValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static Double parseDouble(String str) {
		Double result = null;
		if (str == null || str.isEmpty()) {
			return result;
		}

		try {
			result = numberFormat.parse(str).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}
}
