package br.com.dabage.investments.repositories;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.carteira.PortfolioItemTO;

public interface PortfolioItemRepository extends MongoRepository<PortfolioItemTO, BigInteger> {

	@SuppressWarnings("unchecked")
	PortfolioItemTO save (PortfolioItemTO portfolioItem);

	PortfolioItemTO findByIdCarteiraAndStock(BigInteger idCarteira, String stock);

	List<PortfolioItemTO> findByStock(String stock);
}
