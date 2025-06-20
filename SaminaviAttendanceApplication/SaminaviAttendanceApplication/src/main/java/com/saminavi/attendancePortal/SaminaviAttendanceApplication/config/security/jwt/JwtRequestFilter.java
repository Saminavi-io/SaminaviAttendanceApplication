package com.saminavi.attendancePortal.SaminaviAttendanceApplication.config.security.jwt; // Adjust package as needed

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.saminavi.attendancePortal.SaminaviAttendanceApplication.service.EmployeeService; // Assuming EmployeeService implements UserDetailsService

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final EmployeeService userDetailsService; // Inject your UserDetailsService
    private final JwtUtil jwtUtil; // Inject your JwtUtil

    @Autowired
    public JwtRequestFilter(@Lazy EmployeeService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("[JWT DEBUG] Authorization header: " + authorizationHeader);

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("[JWT DEBUG] Extracted JWT: " + jwt);
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("[JWT DEBUG] Extracted username: " + username);
            } catch (IllegalArgumentException e) {
                System.out.println("[JWT DEBUG] Unable to get JWT Token: " + e.getMessage());
            } catch (ExpiredJwtException e) {
                System.out.println("[JWT DEBUG] JWT Token has expired: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[JWT DEBUG] Error parsing JWT: " + e.getMessage());
            }
        } else {
            System.out.println("[JWT DEBUG] JWT Token does not begin with Bearer String or is missing");
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                System.out.println("[JWT DEBUG] Loaded user details for: " + username);
                // if token is valid configure Spring Security to manually set authentication
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    System.out.println("[JWT DEBUG] JWT is valid for user: " + username);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    System.out.println("[JWT DEBUG] JWT is NOT valid for user: " + username);
                }
            } catch (UsernameNotFoundException e) {
                System.out.println("[JWT DEBUG] User not found in database: " + username);
            } catch (Exception e) {
                System.out.println("[JWT DEBUG] Exception during user details loading or token validation: " + e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}

