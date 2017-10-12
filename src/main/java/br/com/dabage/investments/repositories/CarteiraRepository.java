package br.com.dabage.investments.repositories;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.carteira.CarteiraTO;
import br.com.dabage.investments.user.UserTO;

public interface CarteiraRepository extends MongoRepository<CarteiraTO, BigInteger> {

	@SuppressWarnings("unchecked")
	CarteiraTO save (CarteiraTO carteiraTO);

	List<CarteiraTO> findByUser(UserTO user);
	
	CarteiraTO findByUserAndName(UserTO user, String name);
	
	CarteiraTO findByName(String name);
}
