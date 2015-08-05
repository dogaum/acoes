package br.com.dabage.investments.controller;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.component.chart.Chart;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.OhlcChartModel;
import org.primefaces.model.chart.OhlcChartSeries;
import org.springframework.stereotype.Controller;

import br.com.dabage.investments.company.CompanyTO;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.quote.Quote;
import br.com.dabage.investments.repositories.CompanyRepository;

@Controller(value="chartView")
@RequestScoped
public class ChartView extends BasicView implements Serializable {

    /** */
	private static final long serialVersionUID = -5161552188823251622L;

	private OhlcChartModel grafico;

	static SimpleDateFormat unFormatCompleteDate = new SimpleDateFormat("\"MM/dd/yyyy\"");

	@Resource
	CompanyRepository companyRepository;

	@Resource
	GetQuotation getQuotation;

	private String stock = "IBOV";

	@PostConstruct
	public void init() {
		createGrafico();
	}
	
    public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

    public OhlcChartModel getGrafico() {
		return grafico;
	}

	public void createGrafico() {
		grafico = new OhlcChartModel();

		List<Quote> quotes = getQuotation.getQuoteHist(stock);
		if (quotes == null || quotes.isEmpty()) {
			addInfoMessage("Codigo nao encontrado: " + stock.toUpperCase());
		}

		for (Quote quote : quotes) {
			grafico.add(new OhlcChartSeries(unFormatCompleteDate.format(quote.getDate()), quote.getOpen(),
					quote.getHigh(), quote.getLow(), quote.getClose()));
		}

        grafico.setTitle(stock.toUpperCase());
        grafico.getAxis(AxisType.Y).setLabel("R$");

        DateAxis axis = new DateAxis("Data");
        axis.setTickAngle(-50);
        axis.setTickFormat("%#d/%m/%y");
        grafico.getAxes().put(AxisType.X, axis);
        
        grafico.setAnimate(true);
        grafico.setCandleStick(true);
        grafico.setZoom(true);
    }

    public List<String> completeStock(String stock) {
        List<String> results = new ArrayList<String>();
        List<CompanyTO> companies = companyRepository.findByPrefixLike(stock.toUpperCase());
        if (companies != null) {
        	for (CompanyTO companyTO : companies) {
				results.add(companyTO.getTicker());
			}
        }

        return results;
    }

    public void itemSelect(ItemSelectEvent event) {
    	Chart chart = (Chart) event.getSource();
        OhlcChartModel ohlc = (OhlcChartModel) chart.getModel();
        OhlcChartSeries series = ohlc.getData().get(event.getItemIndex());
        String retorno = "Abertura: " + series.getOpen() + "\nMinima: " + series.getLow();

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, retorno, retorno);        
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}