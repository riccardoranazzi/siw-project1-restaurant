package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.ServerRequest.Headers;

import it.uniroma3.siw.service.ImageService;

//ImageController.java
@RestController
@RequestMapping("/images")
public class ImageController {
 @Autowired
 private ImageService imageService;
 
 @Transactional
 @GetMapping("/{id}")
 public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
	 byte[] imageData = imageService.getImage(id);
	 HttpHeaders headers = new HttpHeaders();
	 headers.setContentType(MediaType.IMAGE_JPEG);
     return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
 }
 
 
}
