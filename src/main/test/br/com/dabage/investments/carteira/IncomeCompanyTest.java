package br.com.dabage.investments.carteira;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IncomeCompanyTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String query = "PROVENTOS";
		String endFII = "http://www2.bmfbovespa.com.br/Agencia-Noticias/ListarNoticias.aspx?idioma=pt-br&q=" + query + "&tipoFiltro=3&periodoDe=INICIO&periodoAte=FIM&pg=";
		String prefix = "http://www2.bmfbovespa.com.br/Agencia-Noticias/";

		String INICIO = "2016-05-02";
		endFII = endFII.replaceFirst("INICIO", INICIO);

		String FIM = "2016-05-02";
		endFII = endFII.replaceFirst("FIM", FIM);

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

					doc = Jsoup.connect(prefix + href).get();
					Element news = doc.getElementById("contentNoticia");
					String total = news.text();
					String[] companies = total.split("EMPRESA: ");
					
					for (int i = 1; i < companies.length; i++) {
						String company = companies[i];

						if (!company.contains("RENDIMENTO")) {
							continue;
						}
						
						String[] lines = company.split("\r");
						System.out.println(lines[3].substring(14, 21).trim());
						System.out.println(lines[3].substring(60, 70));
						System.out.println(lines[4].trim().substring(10, 23));
						System.out.println(lines[5].trim().substring(10, 20));
						
						System.out.println("=======");
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
