package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
