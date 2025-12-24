package com.ftn.pki.security;

import com.ftn.pki.util.TokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


//mozda ne treba
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private TokenUtil tokenUtils;
    private UserDetailsService userDetailsService;
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass()); // za logove

    public TokenAuthenticationFilter(TokenUtil tokenUtils, UserDetailsService userDetailsService) {
        this.tokenUtils = tokenUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String username;

        // 1. getting JWT from request
        String authToken = tokenUtils.getToken(request);

        try {

            if (authToken != null && !authToken.isEmpty()) {

                // 2. Getting username from JWT
                username = tokenUtils.getUsernameFromToken(authToken);

                if (username != null) {

                    // 3. Getting user by username
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 4. Checks if forwarded token is valid
                    if (tokenUtils.validateToken(authToken, userDetails)) {

                        // 5. Create authentication
                        TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
                        authentication.setToken(authToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

        } catch (ExpiredJwtException ex) {
            LOGGER.debug("Token expired!");
        }

        // forward request to next filter
        chain.doFilter(request, response);
    }
}
