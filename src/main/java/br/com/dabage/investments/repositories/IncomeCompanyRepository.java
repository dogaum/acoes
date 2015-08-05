package br.com.dabage.investments.repositories;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.company.IncomeCompanyTO;

public interface IncomeCompanyRepository extends MongoRepository<IncomeCompanyTO, BigInteger> {

	IncomeCompanyTO findOne(BigInteger id);

	@SuppressWarnings("unchecked")
	IncomeCompanyTO save (IncomeCompanyTO incomeCompanyTO);

	List<IncomeCompanyTO> findByIdCompany(BigInteger idCompany);

	IncomeCompanyTO findTopByOrderByIncomeDateDesc();

	IncomeCompanyTO findTopByStockOrderByIncomeDateDesc(String stock);
	
	IncomeCompanyTO findByStockAndYearMonth(String stock, Integer YearMonth);

	List<IncomeCompanyTO> findByYearMonth(Integer YearMonth);
}
