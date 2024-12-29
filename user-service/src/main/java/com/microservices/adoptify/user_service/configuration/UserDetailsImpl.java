package com.microservices.adoptify.user_service.configuration;

import com.microservices.adoptify.user_service.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private Long userId;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // Constructor
    public UserDetailsImpl(Long userId, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // Factory method to build UserDetailsImpl from User entity
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRoles()));
        return new UserDetailsImpl(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    // Getters for additional fields
    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    // Overridden methods from UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Implement other methods as needed
    @Override
    public boolean isAccountNonExpired() {
        return true; // Customize based on your requirements
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Customize based on your requirements
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Customize based on your requirements
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equalsIgnoreCase(status()); // Link to user's status
    }

    private String status() {
        // Implement logic to retrieve user's status, possibly via User entity
        return "ACTIVE"; // Placeholder
    }
}
