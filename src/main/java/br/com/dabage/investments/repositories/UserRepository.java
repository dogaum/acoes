package br.com.dabage.investments.repositories;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.user.UserTO;

public interface UserRepository extends MongoRepository<UserTO, BigInteger> {

	UserTO findOne(BigInteger id);

	@SuppressWarnings("unchecked")
	UserTO save (UserTO user);

	UserTO findByUsername(String username);

	UserTO findByEmail(String email);
}
