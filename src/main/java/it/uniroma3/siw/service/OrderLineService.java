package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.OrderLine;
import it.uniroma3.siw.repository.OrderLineRepository;

@Service
public class OrderLineService {
	
	@Autowired OrderLineRepository orderLineRepository;

	public Iterable<OrderLine> findAll(){
		return this.orderLineRepository.findAll();
	}
	
	public OrderLine findById(Long id) {
		return this.orderLineRepository.findById(id).get();
	}

	public void saveOrderLine(OrderLine orderLine) {
		this.orderLineRepository.save(orderLine);
		
	}

	public void deleteOrderLine(OrderLine orderLine) {
		this.orderLineRepository.delete(orderLine);
		
	}
}
