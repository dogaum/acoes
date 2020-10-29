package br.com.dabage.investments.company;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.config.StockTypeTO;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.StockTypeRepository;

@Component
public class InsertFIITickers {

	private Logger log = LogManager.getLogger(InsertFIITickers.class);

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	StockTypeRepository stockTypeRepository;
	
	/**
	 * @param args
	 */
	public void run() {
			String bmfTickers = "http://www2.bmfbovespa.com.br/Fundos-Listados/FundosListados.aspx?tipoFundo=imobiliario&Idioma=pt-br";
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
					System.out.println(tickerFull);

					CompanyTO obj = companyRepository.findByTicker(tickerFull);
					if (obj == null) {
						if (!tickerFull.endsWith("B")) {
							obj = companyRepository.findByTicker(tickerFull + "B");
							
							if (obj == null) {
								
								if (tickerFull.equals("AGCX11") 
										|| tickerFull.equals("AEFI11")
										|| tickerFull.equals("HGJHX11")) {
									continue;
								}

								CompanyTO company = new CompanyTO(tickerFull, name.text(), fullName.text());
								company.setStockType(stockType);
								company.setCategory("FII");
								log.info("New FII: " + company);
								companyRepository.save(company);								
							}
						}
					}
				}
			} catch (IOException e) {
				log.error(e);
			}

	}

}
