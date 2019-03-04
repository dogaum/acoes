package br.com.dabage.investments.news;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ExtractIdNoticia {

	static NumberFormat numberFormat = new DecimalFormat ("#,##0.00", new DecimalFormatSymbols (new Locale ("pt", "BR")));

	public static void main(String[] args) {
		String header = "http://www2.bmfbovespa.com.br/Agencia-Noticias/ListarNoticias.aspx?idioma=pt-br&idNoticia=760407&header=201810290905PROVENTOS+DOS+EMISSORES+COTADOS+NA+FORMA+%5cEX%5c%3a+29%2f10%2f2018760407&tk=02491c76bfb3aab4ef1522f9137adb91&WT.ac=PROVENTOS+DOS+EMISSORES+COTADOS+NA+FORMA+%5cEX%5c%3a+29%2f10%2f2018";
		String idNoticia = "";
		int initialIndex = header.indexOf("idNoticia=");
		int finalIndex = header.indexOf("&header");
		idNoticia = header.substring(initialIndex +10, finalIndex);

		//System.out.println(idNoticia);

		String url = "https://www.itaucorretora.com.br/Finder/Finder/?stock=MFII11";
		
		try {
			Connection connection = Jsoup.connect(url);
			connection.ignoreHttpErrors(true);
			connection.timeout(1000);

			Document doc = connection.get();
			Elements classes = doc.getElementsByClass("preco");
			System.out.println(classes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Double value = null;
		try {
			value = numberFormat.parse("7,00").doubleValue();
			System.out.println("Valor: " + value);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
}
