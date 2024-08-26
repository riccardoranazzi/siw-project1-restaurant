package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Address;
import it.uniroma3.siw.repository.AddressRepository;
import jakarta.transaction.Transactional;


@Service
public class AddressService {

	@Autowired AddressRepository addressRepository;
	
	 @Transactional
	 public Address saveAddress(Address address) {
		return this.addressRepository.save(address);		
	}

}
