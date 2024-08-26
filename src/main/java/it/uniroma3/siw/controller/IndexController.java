package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ProductService;

@Controller
public class IndexController {
	
	@Autowired CredentialsService credentialsService;
	
	@Autowired ProductService productService;
	
    
    @GetMapping("/menuAdmin")
	public String showMenuAdmin(Model model) {
		model.addAttribute("products", this.productService.getAllProducts());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername());
        model.addAttribute("username", credentials.getUsername());
		model.addAttribute("product", new Product());
		return "admin/menuAdmin";
	}
}