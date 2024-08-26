package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Credentials;

@Repository
public interface CredentialsRepository extends CrudRepository<Credentials, Long> {

	boolean existsByUsername(String username);
	
	Credentials findByUsername(String username);
	
}
