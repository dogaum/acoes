package br.com.dabage.investments.company;

import java.io.IOException;

import javax.annotation.Resource;

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
public class InsertTickers {

	@Autowired
	CompanyRepository companyRepository;

	@Resource
	StockTypeRepository stockTypeRepository;

	/**
	 * @param args
	 */
	public void run() {
			String uolTickers = "http://cotacoes.economia.uol.com.br/acoes-bovespa.html?exchangeCode=.BVSP&page=1&size=2000";
			boolean first = true;
			StockTypeTO stockType = stockTypeRepository.findByName("ACOES");
			try {
				Document doc = Jsoup.connect(uolTickers).get();
				Element pagina = doc.getElementById("resultado-busca");
				Elements lis = pagina.getElementsByTag("li");
				for (Element li : lis) {
					if (first) {
						first = false;
						continue;
					}
					String[] infos = li.text().split(" ");
					String tickerFull = infos[infos.length-1];
					String ticker = tickerFull.substring(0, tickerFull.length() - 3); 
					String companyName = "";
					for (int i = 0; i < infos.length - 1; i++) {
						companyName += infos[i] + " ";
					}

					CompanyTO obj = companyRepository.findByTicker(ticker);
					if (obj == null) {
						CompanyTO companyTO = new CompanyTO(ticker, companyName, companyName);
						companyTO.setStockType(stockType);
						companyRepository.save(companyTO);
						System.out.println(companyTO);						
					} else {
						System.out.println(obj.getTicker());
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

	}

}
