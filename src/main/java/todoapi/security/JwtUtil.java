package todoapi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
// Why this file exists: One place responsible for everything JWT-related — creating tokens and validating them.
@Component  // ← tells Spring to manage this class
public class JwtUtil {

    // Reads jwt.secret from application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Reads jwt.expiration from application.properties
    @Value("${jwt.expiration}")
    private long expiration;

    // Converts the secret string into a secure key object
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Creates a new JWT token for a given email
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)                                    // who this token belongs to
                .issuedAt(new Date())                              // when it was created
                .expiration(new Date(System.currentTimeMillis() + expiration)) // when it expires
                .signWith(getSigningKey())                         // sign it with our secret
                .compact();                                        // build the token string
    }

    // Reads the email out of a token
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Checks if a token is valid and not expired
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;   // no exception = token is valid
        } catch (Exception e) {
            return false;  // any exception = token is invalid or expired
        }
    }
}