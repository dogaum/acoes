package br.com.dabage.investments.news;

import org.primefaces.model.SelectableDataModel;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.dabage.investments.repositories.AbstractDocument;

@Document(collection="news")
@CompoundIndexes({
    @CompoundIndex(name = "news_hd__idx", def = "{'newsHeader': 1, 'newsDate': 1}")
})
public class NewsTO extends AbstractDocument implements SelectableDataModel<NewsTO>{

	@TextIndexed private String newsHeader;

	private String newsDate;

	private String stockType;

	@Indexed
	private String ticker;
	
	private String news;

	private String newsHref;

	public String getNewsHeader() {
		return newsHeader;
	}

	public void setNewsHeader(String newsHeader) {
		this.newsHeader = newsHeader;
	}

	public String getNewsDate() {
		return newsDate;
	}

	public void setNewsDate(String newsDate) {
		this.newsDate = newsDate;
	}

	public String getStockType() {
		return stockType;
	}

	public void setStockType(String stockType) {
		this.stockType = stockType;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getNews() {
		return news;
	}

	public void setNews(String news) {
		this.news = news;
	}

	public String getNewsHref() {
		return newsHref;
	}

	public void setNewsHref(String newsHref) {
		this.newsHref = newsHref;
	}

	@Override
	public String toString() {
		return "NewsBean [newsHeader=" + newsHeader + ", newsDate=" + newsDate
				+ ", stockType=" + stockType + ", ticker=" + ticker + ", news="
				+ news + ", newsHref=" + newsHref + "]";
	}

	@Override
	public NewsTO getRowData(String arg0) {
		return this;
	}

	@Override
	public Object getRowKey(NewsTO arg0) {
		return newsHeader;
	}
}
