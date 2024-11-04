package com.sopotek.aipower;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service  // This registers the class as a Spring bean
public class TokenValidator {
    private static final Logger logger = Logger.getLogger(TokenValidator.class.getName());

    public TokenValidator() {
        // Initialization if necessary
    }

    public boolean validateToken(String token) {
        try {
            // Parse and validate the token
            Claims claims = Jwts.parser()
                    .setSigningKey("ai.power.security.secret_key")
                    .parseClaimsJws(token)
                    .getBody();

            return claims != null;

        } catch (ExpiredJwtException e) {
            logger.info("Token has expired.");
            return false;
        } catch (Exception e) {
            logger.info("Token signature invalid.");
            return false;
        }
    }
}