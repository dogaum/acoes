package br.com.dabage.investments.company;

import java.io.IOException;

import javax.annotation.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.config.StockTypeTO;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.StockTypeRepository;

@Component
public class InsertFIITickers {

	@Resource
	CompanyRepository companyRepository;

	@Resource
	StockTypeRepository stockTypeRepository;
	
	/**
	 * @param args
	 */
	public void run() {
			String bmfTickers = "http://bmfbovespa.com.br/Fundos-Listados/FundosListados.aspx?tipoFundo=imobiliario&Idioma=pt-br";
			boolean first = true;
			StockTypeTO stockType = stockTypeRepository.findByName("FII");
			try {
				Document doc = Jsoup.connect(bmfTickers).get();
				Element pagina = doc.getElementById("ctl00_contentPlaceHolderConteudo_divResultado");
				Elements trs = pagina.getElementsByTag("tr");
				trs.text();
				for (Element tr : trs) {
					if (first) {
						first = false;
						continue;
					}
					Elements tds = tr.children();
					Element fullName = tds.get(0);
					Element name = tds.get(1);
					Element segment = tds.get(2);
					Element ticker = tds.get(3);
					String tickerFull = ticker.text();
					if (segment.text().isEmpty()) {
						tickerFull += "11";
					} else {
						tickerFull += "11B";
					}
					
					CompanyTO obj = companyRepository.findByTicker(tickerFull);
					if (obj == null) {
						CompanyTO company = new CompanyTO(tickerFull, name.text(), fullName.text());
						company.setStockType(stockType);
						System.out.println(company);
						companyRepository.save(company);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

	}

}
