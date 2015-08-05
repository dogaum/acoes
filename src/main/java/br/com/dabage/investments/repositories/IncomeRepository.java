package br.com.dabage.investments.repositories;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.carteira.IncomeTO;

public interface IncomeRepository extends MongoRepository<IncomeTO, BigInteger> {

	IncomeTO findOne(BigInteger id);

	@SuppressWarnings("unchecked")
	IncomeTO save (IncomeTO incomeTO);

	List<IncomeTO> findByIdCarteira(BigInteger idCarteira);

	IncomeTO findTopByOrderByIncomeDateDescAddDateDesc();
}
