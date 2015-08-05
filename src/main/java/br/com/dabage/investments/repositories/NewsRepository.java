package br.com.dabage.investments.repositories;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.news.NewsTO;

public interface NewsRepository extends MongoRepository<NewsTO, BigInteger> {

	NewsTO findOne(BigInteger id);

	@SuppressWarnings("unchecked")
	NewsTO save (NewsTO newsTO);

	List<NewsTO> findByTickerIgnoreCase(String ticker);

	NewsTO findByNewsHeaderAndNewsDate(String newsHeader, String newsDate);

}
