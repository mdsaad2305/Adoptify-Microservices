package com.microservices.adoptify.user_service.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

  private final SecretKey key;

  public JWTService() {
    String secret = "J1ujr3+5s9cyzHX+IaiKDhj75HFCwNpiFqMam/3U4aw=";

    byte[] keyBytes = Decoders.BASE64.decode(secret);

    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(Long userId) {
    Map<String, Object> claims = new HashMap<>();

    return Jwts.builder()
            .claims()
            .add(claims)
            .subject(userId.toString())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 60 * 30))
            .and()
            .signWith(key)
            .compact();
  }

  public String extractUserId(String jwtToken) {
    return extractClaim(jwtToken, Claims::getSubject);
  }

  public boolean validateToken(String jwtToken, UserDetailsImpl userDetails) {
    final String userId = extractUserId(jwtToken);
    return (userId.equals(userDetails.getUserId().toString()) && !isTokenExpired(jwtToken));
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }

  private boolean isTokenExpired(String jwtToken) {
    return extractExpiration(jwtToken).before(new Date());
  }

  private Date extractExpiration(String jwtToken) {
    return extractClaim(jwtToken, Claims::getExpiration);
  }
}
