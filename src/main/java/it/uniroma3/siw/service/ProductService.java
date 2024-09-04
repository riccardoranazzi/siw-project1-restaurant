package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.ProductRepository;

@Service
public class ProductService {
	
 @Autowired
 private ProductRepository productRepository;
 @Autowired
 private ImageRepository imageRepository;
 
 @Transactional
 public Product createProduct(String name, float price, String description, MultipartFile imageFile) throws IOException {
     Image image = new Image();
     image.setImageData(imageFile.getBytes());
     image.setType(imageFile.getContentType());
     image.setName(name);
     image = imageRepository.save(image);
     Product product = new Product();
     product.setDescription(description);
     product.setPrice(price);
     product.setName(name);
     product.setImage(image);
     return productRepository.save(product);
 }
 
 public List<Product> getAllProducts() {
     return productRepository.findAll();
 }

 public Product findById(Long id) {
	return productRepository.findById(id).get();
 }

 public void deleteById(Long id) {
	productRepository.deleteById(id);
	
 }

}