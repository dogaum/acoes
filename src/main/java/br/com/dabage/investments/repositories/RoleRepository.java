package br.com.dabage.investments.repositories;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.dabage.investments.user.RoleTO;

public interface RoleRepository extends MongoRepository<RoleTO, BigInteger> {

	RoleTO findOne(BigInteger id);

	@SuppressWarnings("unchecked")
	RoleTO save (RoleTO role);

	RoleTO findByName(String name);
}
