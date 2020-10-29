package br.com.dabage.investments.news;

import java.security.cert.CertificateException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ExtractIdNoticiaB3 {

	static NumberFormat numberFormat = new DecimalFormat ("#,##0.00", new DecimalFormatSymbols (new Locale ("pt", "BR")));

	public static void main(String[] args) {
		String texto = "fii";
		String dataInicial = "2019-12-02";
		String dataFinal = "2019-12-02";
		String url = "https://sistemasweb.b3.com.br/PlantaoNoticias/Noticias/ListarTitulosNoticias?agencia=18&palavra="+ texto + "&dataInicial=" + dataInicial + "&dataFinal=" + dataFinal;

		String idNoticia = "";
		String dateTime = "";
		String headline = "";

		try {
		    /*
		     *  fix for
		     *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
		     *       sun.security.validator.ValidatorException:
		     *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
		     *               unable to find valid certification path to requested target
		     */
		    TrustManager[] trustAllCerts = new TrustManager[] {
		       new X509TrustManager() {
		          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		            return null;
		          }

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}

		       }
		    };

		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		    // Create all-trusting host name verifier
		    HostnameVerifier allHostsValid = new HostnameVerifier() {
		        public boolean verify(String hostname, SSLSession session) {
		          return true;
		        }
		    };
		    // Install the all-trusting host verifier
		    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		    /*
		     * end of the fix
		     */

			Connection connection = Jsoup.connect(url);
			connection.ignoreHttpErrors(true);
			connection.timeout(1000);

			Document doc = connection.get();
			String result = doc.body().text();

			JSONArray arr = new JSONArray(result);
			for(int i = 0; i < arr.length(); i++){
				idNoticia = arr.getJSONObject(i).getJSONObject("NwsMsg").getString("id");
				dateTime = arr.getJSONObject(i).getJSONObject("NwsMsg").getString("dateTime");
				headline = arr.getJSONObject(i).getJSONObject("NwsMsg").getString("headline");
				String urlItem = String.format("https://sistemasweb.b3.com.br/PlantaoNoticias/Noticias/Detail?idNoticia=%s&agencia=18&dataNoticia=%s", idNoticia, dateTime);
				System.out.println("Noticia: " + headline);
				
				connection = Jsoup.connect(urlItem);
				connection.ignoreHttpErrors(true);
				connection.timeout(1000);

				Document docItem = connection.get();
				String bodyItem = docItem.getElementById("conteudoDetalhe").getAllElements().text();
				int indexIni = bodyItem.indexOf("http");
				int indexFin = bodyItem.lastIndexOf("flnk");
				String linkPdf = bodyItem.substring(indexIni, indexFin-1);
				linkPdf = linkPdf.replaceFirst("https", "http");
				linkPdf = linkPdf.replaceAll("visualizarDocumento", "exibirDocumento");
				System.out.println(linkPdf);
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
