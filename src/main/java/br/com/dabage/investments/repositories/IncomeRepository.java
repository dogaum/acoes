package br.com.dabage.investments.repositories;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.carteira.IncomeTO;

public interface IncomeRepository extends MongoRepository<IncomeTO, BigInteger> {

	@SuppressWarnings("unchecked")
	IncomeTO save (IncomeTO incomeTO);

	List<IncomeTO> findByIdCarteira(BigInteger idCarteira);

	List<IncomeTO> findByIdCarteiraAndType(BigInteger idCarteira, String type);

	List<IncomeTO> findByIdCarteiraAndTypeAndStock(BigInteger idCarteira, String type, String stock);
	
	List<IncomeTO> findFirstByOrderByIncomeDateDescAddDateDesc();

	List<IncomeTO> findByStockOrderByIncomeDateAsc(String stock);

	IncomeTO findTopByIdCarteiraOrderByIncomeDateDesc(BigInteger idCarteira);
}
