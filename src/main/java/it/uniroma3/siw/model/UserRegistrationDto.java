package it.uniroma3.siw.model;

import jakarta.validation.Valid;

public class UserRegistrationDto {
	
    @Valid
    private User user;

    @Valid
    private Credentials credentials;

    @Valid
    private Address address;

    // Getters e Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
