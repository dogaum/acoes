package br.com.dabage.investments.repositories;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.company.InformeMensalTO;

public interface InformeMensalRepository extends MongoRepository<InformeMensalTO, BigInteger> {

	@SuppressWarnings("unchecked")
	InformeMensalTO save (InformeMensalTO informeMensalTO);

	List<InformeMensalTO> findByTickerLike(String tickerLike);

	InformeMensalTO findByTicker(String ticker);
	
	InformeMensalTO findByTickerAndMesCompetencia(String ticker, String mesCompetencia);
}
