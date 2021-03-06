package br.com.dabage.investments.repositories;

import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.carteira.NegotiationTO;
import br.com.dabage.investments.carteira.NegotiationType;

public interface NegotiationRepository extends MongoRepository<NegotiationTO, BigInteger> {

	@SuppressWarnings("unchecked")
	NegotiationTO save (NegotiationTO negotiationTO);

	List<NegotiationTO> findByIdCarteira(BigInteger idCarteira);

	LinkedList<NegotiationTO> findByIdCarteiraAndStockOrderByDtNegotiationAsc(BigInteger idCarteira, String stock);

	List<NegotiationTO> findByNegotiationType(NegotiationType negotiationType);

	List<NegotiationTO> findByIdCarteiraAndDtNegotiationBetween(BigInteger idCarteira, Date from, Date to);

	List<NegotiationTO> findByIdCarteiraAndDtNegotiationGreaterThanEqual(BigInteger idCarteira, Date from);

	List<NegotiationTO> findByIdCarteiraAndDtNegotiationLessThanEqual(BigInteger idCarteira, Date to);

	List<NegotiationTO> findByStockOrderByDtNegotiationAsc(String stock);

	NegotiationTO findTopByIdCarteiraOrderByDtNegotiationDesc(BigInteger idCarteira);
}
