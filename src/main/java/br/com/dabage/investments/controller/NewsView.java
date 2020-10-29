package br.com.dabage.investments.controller;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;

import br.com.dabage.investments.news.NewsTO;
import br.com.dabage.investments.quote.GetQuotation;
import br.com.dabage.investments.repositories.CompanyRepository;
import br.com.dabage.investments.repositories.NewsRepository;

@Controller(value="newsView")
@RequestScoped
public class NewsView extends BasicView implements Serializable {

	/** */
	private static final long serialVersionUID = -2524943863550149439L;

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	GetQuotation getQuotation;

	@Autowired
	NewsRepository newsRepository;

	@Autowired
	MongoTemplate template;
	
	private List<NewsTO> news = new ArrayList<NewsTO>();

	private NewsTO selectedNews;

	/**
	 * Filtes
	 */
	private String filterDate;
	private String filterSymbol;
	private String filterSubject;

	public String init() {
		clearFilter(null);
		return "fr";
	}

	public void clearFilter(ActionEvent event) {
		filterDate = "";
		filterSymbol = "";
		filterSubject = "";
	}

	public void apply(ActionEvent event) {
		Query query = new Query();

		if (filterSubject != null && !filterSubject.isEmpty()) {
			//TextCriteria criteria = TextCriteria.forDefaultLanguage().matching(filterSubject.toUpperCase());
			//query = TextQuery.queryText(criteria);
			query.addCriteria(Criteria.where("newsHeader").regex(filterSymbol.toUpperCase()));
		}

		if (filterSymbol != null && !filterSymbol.isEmpty()) {
			query.addCriteria(Criteria.where("ticker").regex(filterSymbol.toUpperCase()));
		}

		if (filterDate != null && !filterDate.isEmpty()) {
			query.addCriteria(Criteria.where("newsDate").regex(filterDate.toUpperCase()));
		}

		news = template.find(query, NewsTO.class);
	}
	
    public List<NewsTO> getNews() {
		return news;
	}

	public void setNews(List<NewsTO> news) {
		this.news = news;
	}

	public NewsTO getSelectedNews() {
		return selectedNews;
	}

	public void setSelectedNews(NewsTO selectedNews) {
		this.selectedNews = selectedNews;
	}

	public String getFilterDate() {
		return filterDate;
	}

	public void setFilterDate(String filterDate) {
		this.filterDate = filterDate;
	}

	public String getFilterSymbol() {
		return filterSymbol;
	}

	public void setFilterSymbol(String filterSymbol) {
		this.filterSymbol = filterSymbol;
	}

	public String getFilterSubject() {
		return filterSubject;
	}

	public void setFilterSubject(String filterSubject) {
		this.filterSubject = filterSubject;
	}

}