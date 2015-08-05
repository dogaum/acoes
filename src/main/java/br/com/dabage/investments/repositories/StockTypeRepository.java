package br.com.dabage.investments.repositories;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.config.StockTypeTO;

public interface StockTypeRepository extends MongoRepository<StockTypeTO, BigInteger> {

	StockTypeTO findOne(BigInteger id);

	@SuppressWarnings("unchecked")
	StockTypeTO save (StockTypeTO stockTypeTO);

	List<StockTypeTO> findByRemoveDateNull();

	StockTypeTO findByName(String name);
}
