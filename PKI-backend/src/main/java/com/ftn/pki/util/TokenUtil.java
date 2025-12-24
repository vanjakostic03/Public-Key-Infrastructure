package com.ftn.pki.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
//import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenUtil {

    //who issues the token
    @Value("public-key-infrastructure")
    private String APP_NAME;

    //secret that only backend app should know - to check if token changed
    @Value("somesecret")
    public String SECRET;

    //when expires in miliseconds
    @Value("10800000")
    private int EXPIRES_IN;

    //The name of the header through which the JWT will be forwarded in the server-client communication
    @Value("Authorization")
    private String AUTH_HEADER;

    //client is web
    private static final String AUDIENCE_WEB = "web";

    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;


    public String generateToken(com.ftn.pki.entities.users.User user) {
        return Jwts.builder()
                .setIssuer(APP_NAME)
                .setSubject(user.getEmail())
                .setAudience(generateAudience())
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate())
                .claim("role", user.getUserType())
                .claim("id", user.getId())
                .signWith(SIGNATURE_ALGORITHM, SECRET).compact();


        // moguce je postavljanje proizvoljnih podataka u telo JWT tokena pozivom funkcije .claim("key", value), npr. .claim("role", user.getRole())
    }


    private String generateAudience() {

        //	Moze se iskoristiti org.springframework.mobile.device.Device objekat za odredjivanje tipa uredjaja sa kojeg je zahtev stigao.
        //	https://spring.io/projects/spring-mobile

//        String audience = AUDIENCE_UNKNOWN;
//            if (device.isNormal()) {
//                audience = AUDIENCE_WEB;
//            }else if (device.isMobile()) {
//                audience = AUDIENCE_MOBILE;
//            }
//        return audience;
        return AUDIENCE_WEB;
    }

    private Date generateExpirationDate() {
        return new Date(new Date().getTime() + EXPIRES_IN);
    }


    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }


    public String getUsernameFromToken(String token) {
        String username;

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            username = null;
        }

        return username;
    }


    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    public String getAudienceFromToken(String token) {
        String audience;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            audience = claims.getAudience();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            expiration = null;
        }

        return expiration;
    }


    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            claims = null;
        }

        // Preuzimanje proizvoljnih podataka je moguce pozivom funkcije claims.get(key)

        return claims;
    }

    /**
     * Funkcija za validaciju JWT tokena.
     *
     * @param token JWT token.
     * @param userDetails Informacije o korisniku koji je vlasnik JWT tokena.
     * @return Informacija da li je token validan ili ne.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {

        final String username = getUsernameFromToken(token);
        final Date expiration = getExpirationDateFromToken(token);

        // Token je validan kada:
        return (username != null // korisnicko ime nije null
                && username.equals(userDetails.getUsername()) // korisnicko ime iz tokena se podudara sa korisnickom imenom koje pise u bazi
                && expiration != null && !isTokenExpired(expiration));
    }

    private Boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }

    public int getExpiredIn() {
        return EXPIRES_IN;
    }


    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }

}
