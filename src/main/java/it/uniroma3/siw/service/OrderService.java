package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Order;
import it.uniroma3.siw.model.OrderLine;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.OrderLineRepository;
import it.uniroma3.siw.repository.OrderRepository;

@Service
public class OrderService {

	  @Autowired OrderRepository orderRepository;
	  
	  @Autowired OrderLineRepository orderLineRepository;

	    public void saveOrder(Order order) {
	        orderRepository.save(order);
	    }

	    public Order findById(Long id) {
	        return orderRepository.findById(id).orElse(null);
	    }

	    @Transactional
	    public void addProductToOrder(Order order, Product product, int quantity) {
	        // Verifica se esiste già una OrderLine per questo prodotto nel contesto dell'ordine attuale
	        OrderLine existingOrderLine = null;
	        for (OrderLine ol : order.getOrderLines()) {
	            if (ol.getProduct().getId().equals(product.getId())) {
	                existingOrderLine = ol;
	                break;
	            }
	        }

	        if (existingOrderLine != null) {
	            // Se esiste, aggiorna la quantità
	            existingOrderLine.setQuantity(existingOrderLine.getQuantity() + quantity);
	            orderLineRepository.save(existingOrderLine);
	        } else {
	            // Se non esiste, crea una nuova OrderLine specifica per questo ordine e prodotto
	            OrderLine newOrderLine = new OrderLine();
	            newOrderLine.setOrder(order);
	            newOrderLine.setProduct(product);
	            newOrderLine.setQuantity(quantity);
	            orderLineRepository.save(newOrderLine);

	            // Aggiungi la nuova OrderLine all'ordine
	            order.getOrderLines().add(newOrderLine);
	        }

	        // Salva l'ordine aggiornato
	        orderRepository.save(order);
	    }
	
	    @Transactional
	    public void decreaseOrRemoveOrderLine(OrderLine orderLine) {
	        if (orderLine.getQuantity() > 1) {
	            // Se la quantità è maggiore di 1, diminuisci la quantità
	            orderLine.setQuantity(orderLine.getQuantity() - 1);
	            orderLineRepository.save(orderLine);
	        } else {
	            // Se la quantità è 1, rimuovi l'OrderLine
	            Order order = orderLine.getOrder();
	            order.getOrderLines().remove(orderLine);
	            orderRepository.save(order);  // Aggiorna l'ordine per rimuovere la relazione
	            orderLineRepository.delete(orderLine);
	        }
	    }

		public void deleteOrder(Order order) {
			orderRepository.delete(order);			
		}
	    
		@Transactional
		public void finalizePreviousOrder(User user) {
	        Order currentOrder = orderRepository.findCurrentOrderByUser(user);
	        if (currentOrder != null && !currentOrder.isFinalized()) {
	            currentOrder.setFinalized(true);
	            orderRepository.save(currentOrder);
	        }
	    }

		@Transactional
	    public Order createNewOrder(User user) {
	        finalizePreviousOrder(user); 
	        Order newOrder = new Order();
	        newOrder.setUser(user);
	        orderRepository.save(newOrder);
	        return newOrder;
	    }

		public Iterable<Order> findAll() {
			return this.orderRepository.findAll();
		}

		public Iterable<Order> getAllFinalizedOrders() {
		return orderRepository.findByFinalizedTrue();
		}
	    
}
