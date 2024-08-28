package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Order;
import it.uniroma3.siw.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	  @Query("SELECT o FROM Order o WHERE o.user = :user AND o.finalized = false")
	    Order findCurrentOrderByUser(@Param("user") User user);

	Iterable<Order> findByFinalizedTrue();
	  
}
