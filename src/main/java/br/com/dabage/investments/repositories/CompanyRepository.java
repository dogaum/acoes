package br.com.dabage.investments.repositories;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.company.CompanyTO;

public interface CompanyRepository extends MongoRepository<CompanyTO, BigInteger> {

	CompanyTO findOne(BigInteger id);

	@SuppressWarnings("unchecked")
	CompanyTO save (CompanyTO companyTO);

	List<CompanyTO> findByPrefix(String prefix);

	List<CompanyTO> findByPrefixLike(String prefixLike);

	CompanyTO findByTicker(String ticker);
}
