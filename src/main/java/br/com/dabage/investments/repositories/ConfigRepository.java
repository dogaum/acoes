package br.com.dabage.investments.repositories;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.config.ConfigTO;
import br.com.dabage.investments.user.UserTO;

public interface ConfigRepository extends MongoRepository<ConfigTO, BigInteger> {

	@SuppressWarnings("unchecked")
	ConfigTO save (ConfigTO user);

	UserTO findByKey(String key);
}
