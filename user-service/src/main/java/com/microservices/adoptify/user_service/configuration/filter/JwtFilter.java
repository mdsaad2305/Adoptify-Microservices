package com.microservices.adoptify.user_service.configuration.filter;

import com.microservices.adoptify.user_service.configuration.JWTService;
import com.microservices.adoptify.user_service.configuration.UserDetailServiceImpl;
import com.microservices.adoptify.user_service.configuration.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JWTService jwtService;
  private final ApplicationContext applicationContext;

  public JwtFilter(JWTService jwtService, ApplicationContext applicationContext) {
    this.jwtService = jwtService;
    this.applicationContext = applicationContext;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    String jwtToken = null;
    String username = null;

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      jwtToken = authHeader.substring(7);
      username = jwtService.extractUserName(jwtToken);
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      UserDetailsImpl userDetails =
          applicationContext.getBean(UserDetailServiceImpl.class).loadUserByUsername(username);

      if (jwtService.validateToken(jwtToken, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response); // go to the next filter
  }
}
