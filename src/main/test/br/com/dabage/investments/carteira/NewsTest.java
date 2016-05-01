package br.com.dabage.investments.carteira;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.com.dabage.investments.news.IncomePdfParser;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class NewsTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String query = "ceoc";
		String endFII = "http://www2.bmfbovespa.com.br/Agencia-Noticias/ListarNoticias.aspx?idioma=pt-br&q=" + query + "&tipoFiltro=3&periodoDe=INICIO&periodoAte=FIM&pg=";
		String prefix = "http://www2.bmfbovespa.com.br/Agencia-Noticias/";

		String INICIO = "2016-04-29";
		endFII = endFII.replaceFirst("INICIO", INICIO);

		String FIM = "2016-04-29";
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
					String newsName = link.text();

					doc = Jsoup.connect(prefix + href).get();
					Element news = doc.getElementById("contentNoticia");
					
					if (news.text().contains("rendimento")) {

						int indexIni = news.text().indexOf("http");
						int indexFin = news.text().lastIndexOf("flnk");
						String linkPdf = news.text().substring(indexIni, indexFin-1);
						linkPdf = linkPdf.replaceFirst("https", "http");
						PdfReader reader = new PdfReader(linkPdf);
						String dados = PdfTextExtractor.getTextFromPage(reader, 1);

						IncomePdfParser parser = new IncomePdfParser(dados);

						System.out.println("Adm: " + parser.getAdministrator());
						System.out.println("Tipo: " + parser.getType());
						System.out.println("Valor: " + parser.getValue());
						System.out.println();
						reader.close();
					}
			        
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
