package br.com.dabage.investments.repositories;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.carteira.NegotiationTO;
import br.com.dabage.investments.carteira.NegotiationType;

public interface NegotiationRepository extends MongoRepository<NegotiationTO, BigInteger> {

	NegotiationTO findOne(BigInteger id);

	@SuppressWarnings("unchecked")
	NegotiationTO save (NegotiationTO negotiationTO);

	List<NegotiationTO> findByIdCarteira(BigInteger idCarteira);

	List<NegotiationTO> findByNegotiationType(NegotiationType negotiationType);

	List<NegotiationTO> findByIdCarteiraAndDtNegotiationBetween(BigInteger idCarteira, Date from, Date to);

	List<NegotiationTO> findByIdCarteiraAndDtNegotiationGreaterThanEqual(BigInteger idCarteira, Date from);

	List<NegotiationTO> findByIdCarteiraAndDtNegotiationLessThanEqual(BigInteger idCarteira, Date to);
}
