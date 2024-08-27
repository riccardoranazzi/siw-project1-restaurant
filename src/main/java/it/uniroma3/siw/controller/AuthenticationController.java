package it.uniroma3.siw.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Address;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.UserRegistrationDto;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.AddressService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
	
	
	@Autowired UserService userService;
	@Autowired CredentialsService credentialsService;
	@Autowired UserRepository userRepository;
	@Autowired CredentialsRepository credentialsRepository;
	@Autowired AddressService addressService;
	@Autowired PasswordEncoder passwordEncoder;
	
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
	
	
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
       model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "formRegister";
    }
    
    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(value="error", required = false) String error) {
    	     	 
        model.addAttribute("credentials", new Credentials());  // Crea un nuovo oggetto Credentials
        
        if(error!=null) {
        	model.addAttribute("errorMessage", "Username o password errati, perfavore riprova.");
        }
    	 
        return "formLogin";
    }

    @GetMapping("/success")
    public String defaultSuccessAfterLogin(Model model) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername());
        model.addAttribute("username", credentials.getUsername());
        
        if(credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
			return "admin/indexAdmin";
		}
		return "index";
    }
    

    @GetMapping("/index")
    public String index(Model model) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Credentials credentials = credentialsService.findByUsername(userDetails.getUsername());
            model.addAttribute("username", credentials.getUsername());
        }
    	if(authentication instanceof AnonymousAuthenticationToken) {
    		return "index";
    	} else {
    		UserDetails userdetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    		Credentials credentials = credentialsService.findByUsername(userdetails.getUsername());
    		if(credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
    			return "admin/indexAdmin";
    		}
    		return "index";
    	}
    }
    
    @GetMapping("/registrationSuccessfull")
    public String registrationSuccessfull() {
    	return "registrationSuccessfull";
    	
    }
    
    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername());
        User user = credentials.getUser();
        
        model.addAttribute("username", credentials.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("credentials", credentials);
        return "profile";
    }
    
    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("errorMessage", "Errore nell'aggiornamento del profile, per favore riprova");
            return "profile";
        }

        // Recupera l'utente attuale dal database per mantenere gli altri dati inalterati
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Credentials credentials = credentialsService.findByUsername(userDetails.getUsername());
        User currentUser = credentials.getUser();

        // Aggiorna solo i campi modificabili
        currentUser.setName(user.getName());
        currentUser.setSurname(user.getSurname());
        currentUser.setCellulare(user.getCellulare());
        currentUser.getAddress().setName(user.getAddress().getName());
        currentUser.getAddress().setZipcode(user.getAddress().getZipcode());

        userService.saveUser(currentUser);
        
        if(!bindingResult.hasErrors()) {
        	model.addAttribute("successMessage", "Profilo aggiornato!");
        	return "profile";
        }

        return "redirect:/profile";
    }
    
    @PostMapping("/registerUser")
    public String registerUser(@Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto userRegistrationDto,
                               BindingResult bindingResult, Model model) {
        User user = userRegistrationDto.getUser();
        Credentials credentials = userRegistrationDto.getCredentials();
        Address address = userRegistrationDto.getAddress();
        
        if (bindingResult.hasErrors()) { 
        	model.addAttribute("errorMessage", "Errore nella registrazione. Per favore controlla i dettagli inseriti.");      
            return "formRegister";
        }
        
        if(!credentialsService.isUsernameUnique(userRegistrationDto.getCredentials().getUsername())){
        	 model.addAttribute("errorMessage", "Username già esistente. Scegli un altro username.");
             return "formRegister";
        }
        try {
            
            credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
            credentials.setUser(user);
            user.setAddress(address);
            credentials.setRole(Credentials.DEFAULT_ROLE);

            logger.info("Saving address: " + address);
            addressService.saveAddress(address);

            logger.info("Saving user: " + user);
            userService.saveUser(user);

            logger.info("Saving credentials: " + credentials);
            credentialsService.saveCredentials(credentials);

            logger.info("Utente salvato correttamente");
            return "redirect:/registrationSuccessfull";
        } catch (Exception e) {
            logger.error("Errore durante registrazione utente", e);
            model.addAttribute("errorMessage", "Errore durante la registrazione, per favore riprova");
            return "formRegister";
        }
    }
    
    
    
    @GetMapping("/about")
	public String about(Model model) {
    	return "about";
	}
    
	
}

