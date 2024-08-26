package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.OrderLine;

public interface OrderLineRepository extends CrudRepository<OrderLine, Long> {

	
}
