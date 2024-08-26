package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ProductService;

@Controller
public class ProductController {
	
 @Autowired
 private ProductService productService;
 
 @Autowired CredentialsService credentialsService;

	@ModelAttribute("username")
    public String addUserToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Credentials credentials = credentialsService.findByUsername(userDetails.getUsername());
            return credentials.getUsername();
        }
        return null; // Nessun utente autenticato
    }
 
	 @PostMapping("/product")
 	 public String createProduct(@RequestParam("name") String name, @RequestParam("description") String description, @RequestParam("price") float price, @RequestParam("image") MultipartFile imageFile) throws IOException { 
	 productService.createProduct(name, price, description, imageFile);
     return "redirect:/menuAdmin";
 }
 
 	@GetMapping("/formNewProduct")
 	public String formNewProduct(Model model) {
 		model.addAttribute("product", new Product());
 		return "admin/formNewProduct";
 	}
 	
	@GetMapping("/menu")
 	public String showMenu(Model model) {
	model.addAttribute("products", productService.getAllProducts());
	return "menu";
 }
	
	@GetMapping("/product/{id}")
	public String showProduct(Model model, @PathVariable ("id") Long id) {
		model.addAttribute("product", this.productService.findById(id));
		return "/product";
	}
	
	@GetMapping("/admin/product/{id}")
	public String showProductAdmin(Model model, @PathVariable ("id") Long id) {
		model.addAttribute("product", this.productService.findById(id));
		return "admin/product";
	}

}
