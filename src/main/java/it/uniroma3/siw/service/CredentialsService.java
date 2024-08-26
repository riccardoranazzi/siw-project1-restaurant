package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import jakarta.transaction.Transactional;

@Service
public class CredentialsService {

	@Autowired protected CredentialsRepository credentialsRepository;
	
	@Autowired protected PasswordEncoder passwordEncoder;
	
	public Credentials getCredentials(Long id) {
		return this.credentialsRepository.findById(id).get();
	}
	
	public boolean isUsernameUnique(String username) {
        return !credentialsRepository.existsByUsername(username);  
    }
	
	public Credentials findByUsername(String username) {
		return this.credentialsRepository.findByUsername(username);
	}
	
	@Transactional
	public Credentials saveCredentials(Credentials credentials){
		return this.credentialsRepository.save(credentials);
	}

	 public User findUserByUsername(String username) {
	        Credentials credentials = this.findByUsername(username);
	        if (credentials != null) {
	            return credentials.getUser();
	        }
	        return null;
	    }
}
