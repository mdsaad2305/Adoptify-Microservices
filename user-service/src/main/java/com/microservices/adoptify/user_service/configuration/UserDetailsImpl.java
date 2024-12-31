package com.microservices.adoptify.user_service.configuration;

import com.microservices.adoptify.user_service.model.User;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {

  private Long userId;
  private String username;
  private String email;
  private String password;
  private Collection<? extends GrantedAuthority> authorities;

  // Constructor
  public UserDetailsImpl(
      Long userId,
      String username,
      String email,
      String password,
      Collection<? extends GrantedAuthority> authorities) {
    this.userId = userId;
    this.username = username;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
  }

  public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRoles()));
    return new UserDetailsImpl(
        user.getUserId(), user.getUsername(), user.getEmail(), user.getPassword(), authorities);
  }

  public Long getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

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
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return "ACTIVE".equalsIgnoreCase(status());
  }

  private String status() {
    return "ACTIVE";
  }
}
