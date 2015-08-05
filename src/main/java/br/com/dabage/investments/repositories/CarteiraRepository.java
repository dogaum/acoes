package br.com.dabage.investments.repositories;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.carteira.CarteiraTO;
import br.com.dabage.investments.user.UserTO;

public interface CarteiraRepository extends MongoRepository<CarteiraTO, BigInteger> {

	CarteiraTO findOne(BigInteger id);

	@SuppressWarnings("unchecked")
	CarteiraTO save (CarteiraTO carteiraTO);

	List<CarteiraTO> findByUser(UserTO user);
}
