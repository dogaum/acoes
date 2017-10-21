package br.com.dabage.investments.news;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.dabage.investments.mail.SendMailSSL;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class CheckNewsTest {

	@Autowired
	public CheckNews checkNews;

	@Test
	public void testRun2() {
		fail("Not yet implemented");
	}

	@Test
	public void testRun() {
		String query = "fii";
		int qtyNews = checkNews.run(query);
	}

	@Test
	public void testCheckIncome() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckIncomes() {
		fail("Not yet implemented");
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
					file);
		}
	}

	@Test
	public void testCheckIncomesByInterval() {
		fail("Not yet implemented");
	}

}
