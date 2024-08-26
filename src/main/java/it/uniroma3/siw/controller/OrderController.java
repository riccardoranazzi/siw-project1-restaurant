package it.uniroma3.siw.controller;

import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Orario;
import it.uniroma3.siw.model.Order;
import it.uniroma3.siw.model.OrderLine;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.OrderRepository;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.OrderLineService;
import it.uniroma3.siw.service.OrderService;
import it.uniroma3.siw.service.ProductService;
import it.uniroma3.siw.service.UserService;

@Controller
public class OrderController {
	
	@Autowired CredentialsRepository credentialsRepository;
	
	@Autowired OrderRepository orderRepository;
	
	@Autowired ProductRepository productRepository;
	
	@Autowired CredentialsService credentialsService;
	
	@Autowired ProductService productService;
	
	@Autowired UserService userService;
	
	@Autowired OrderService orderService;
	
	@Autowired OrderLineService orderLineService;

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

	@GetMapping("/order/{id}")
	public String showOrder(Model model, @PathVariable("id")Long id) {
		model.addAttribute("order", this.orderRepository.findById(id));
		model.addAttribute("orderLines", this.orderRepository.findById(id).get().getOrderLines());
		model.addAttribute("product", this.productRepository.findAll());
		return "/admin/order";
	}
	
	@GetMapping("/selectTime")
    public String showTimeSelectionForm(Model model) {
        model.addAttribute("orari", Orario.values());
        return "selectTime";
    }

	
	@GetMapping("/formNewOrder")
	public String formNewOrder(Model model) {
		model.addAttribute("product", new Product());
		model.addAttribute("products", this.productRepository.findAll());
		model.addAttribute("orderLine", new OrderLine());
		model.addAttribute("order", new Order());
		return "formNewOrder";
	}

	@Transactional
	@PostMapping("/selectTime")
    public String handleTimeSelection(@RequestParam("orario") Orario orario) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        
        // Crea l'ordine e imposta l'orario e l'indirizzo dell'utente loggato
        Order order = orderService.createNewOrder(user);
        order.setOrario(orario);
        order.setUser(user);

        // Salva l'ordine e reindirizza alla pagina per aggiungere prodotti al carrello
        orderService.saveOrder(order);

        return "redirect:/formNewOrder/" + order.getId();
    }
	
	@Transactional
	@GetMapping("/formNewOrder/{orderId}")
	public String showFormNewOrder(@PathVariable("orderId") Long orderId, Model model) {
	    Order ordine = orderService.findById(orderId);
	    model.addAttribute("order", ordine);
		model.addAttribute("products", productService.getAllProducts());
	    return "formNewOrder";
	}
	

	@Transactional
	  @PostMapping("/addProductToOrder/{orderId}")
	    public String addProductToOrder(@PathVariable("orderId") Long orderId, 
	                                    @RequestParam("productId") Long productId, 
	                                    @RequestParam("quantity") int quantity) {
	        // Trova l'ordine e il prodotto dal database
	        Order order = orderService.findById(orderId);
	        Product product = productService.findById(productId);

	        if (order != null && product != null) {
	            // Usa il metodo del servizio per aggiungere il prodotto all'ordine
	            orderService.addProductToOrder(order, product, quantity);
	        }

	        // Redirige alla pagina dell'ordine con l'ID dell'ordine corrente
	        return "redirect:/formNewOrder/" + orderId;
	    }
	  
	  @Transactional
	  @PostMapping("/decreaseOrRemoveProductFromOrder/{orderLineId}")
		public String decreaseOrRemoveProductFromOrder(@PathVariable("orderLineId") Long orderLineId) {
		    OrderLine orderLine = orderLineService.findById(orderLineId);
		    if (orderLine != null) {
		        orderService.decreaseOrRemoveOrderLine(orderLine);
		    }

		    return "redirect:/formNewOrder/" + orderLine.getOrder().getId();
		}
	  
	  @Transactional
	  @PostMapping("/confirmOrder/{orderId}")
	  public String confirmOrder(@PathVariable("orderId") Long orderId, Model model) {
	      Order order = orderService.findById(orderId);	      
	      model.addAttribute("order", order);
	      
	      if(order.getOrderLines().isEmpty() && order!=null){
	    	  model.addAttribute("errorMessage", "Carrello vuoto");
	    	  return "formnewOrder";
	      }
	      
	      if (order != null) {
	    	  order.setFinalized(true);
	          orderService.saveOrder(order);
	          float total = order.getTotalPrice();
	          model.addAttribute("total", total);
	          return "redirect:/completeOrder/" + orderId; 
	      }
	      
	      return "formNewOrder"; 
	  }

	  @Transactional
	  @PostMapping("/cancelOrder/{orderId}")
	  public String cancelOrder(@PathVariable("orderId") Long orderId) {
	      Order order = orderService.findById(orderId);
	      if (order != null) {
	          orderService.deleteOrder(order); 
	      }
	      return "redirect:/index";
	  }
	
	  @Transactional
	  @PostMapping("/completeOrder/{orderId}")
	  public String completeOrder(@PathVariable("orderId") Long orderId, @RequestParam("notes") String notes) {
	      Order order = orderService.findById(orderId);
	      if (order != null) {
	          order.setNotes(notes); 
	          orderService.saveOrder(order); 
	      }
	      return "orderConfirmed"; 
	  }
	  
	  @Transactional
	  @GetMapping("/orderConfirmed/{orderId}")
	  public String orderConfirmed(@PathVariable("orderId") Long orderId, Model model) {
		  Order order = orderService.findById(orderId);
		  float total = order.getTotalPrice();
		  model.addAttribute("total", total);
		  model.addAttribute("order", order);
		  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	      User user = userService.findByUsername(userDetails.getUsername());
	      String address = user.getAddress().getName();
	      model.addAttribute("address", address);
	      model.addAttribute("surname", user.getSurname());
	      
	      return "orderConfirmed";
	  }
	  
	  @Transactional
	  @GetMapping("/completeOrder/{orderId}")
	  public String completeOrder(@PathVariable("orderId") Long orderId, Model model) {
	      Order order = orderService.findById(orderId);
	      float total = order.getTotalPrice();
	      model.addAttribute("order", order);
	      model.addAttribute("total", total);
	      return "completeOrder"; 
	  }
	  
	  @GetMapping("/admin/orders")
	  public String ShowOrders(Model model) {
		  model.addAttribute("orders", orderService.findAll());
		  return "/admin/orders";
	  }
	  
	  @GetMapping("/admin/order/{orderId}")
	  public String showAdminOrder(Model model, @PathVariable("orderId")Long orderId) {
		  Order order = orderService.findById(orderId);
		  model.addAttribute("orderLines", order.getOrderLines());
		  float total = order.getTotalPrice();
		  model.addAttribute("total", total);
		  model.addAttribute("order", order);
		  return "/admin/order";
	  }
	  
}
