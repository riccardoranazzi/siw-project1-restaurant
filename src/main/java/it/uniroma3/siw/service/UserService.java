package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {

	@Autowired UserRepository userRepository;
	
	@Autowired CredentialsService credentialsService;
	
	public User getUser(Long id) {
		return this.userRepository.findById(id).get();
	}
	
	@Transactional
	public User saveUser(User user) {
		return this.userRepository.save(user);
	}
	
	public User findByUsername(String username) {
        return credentialsService.findByUsername(username).getUser();
    }
}
