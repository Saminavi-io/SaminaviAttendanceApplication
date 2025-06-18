package com.saminavi.attendancePortal.SaminaviAttendanceApplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saminavi.attendancePortal.SaminaviAttendanceApplication.config.security.jwt.JwtUtil;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.dto.LoginRequest;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.dto.LoginResponse;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.dto.RegistrationRequest;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.service.EmployeeService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmployeeService employeeService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, 
                         JwtUtil jwtUtil,
                         EmployeeService employeeService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.employeeService = employeeService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(jwt));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        try {
            employeeService.registerEmployee(
                    registrationRequest.getUsername(),
                    registrationRequest.getPassword(),
                    registrationRequest.getName(),
                    registrationRequest.getEmployeeId(),
                    registrationRequest.getEmail()
            );
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}