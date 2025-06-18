package com.saminavi.attendancePortal.SaminaviAttendanceApplication.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain.Employee;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.repository.EmployeeRepository;

@Service
public class EmployeeService implements UserDetailsService {

	private final EmployeeRepository employeeRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository, @Lazy PasswordEncoder passwordEncoder) {
		this.employeeRepository = employeeRepository;
		this.passwordEncoder = passwordEncoder;
	}
    
	// Method to register a new employee
	public Employee registerEmployee(String username, String password, String name, String employeeId, String email) {
		// Check if username exists
		if (employeeRepository.findByUsername(username).isPresent()) {
			throw new RuntimeException("Username already exists");
		}
		// Check if employeeId exists
		if (employeeRepository.findByEmployeeId(employeeId).isPresent()) {
			throw new RuntimeException("Employee ID already exists");
		}
		// Check if email exists
		if (employeeRepository.findByEmail(email).isPresent()) {
			throw new RuntimeException("Email already exists");
		}
		
		Employee employee = new Employee(username, passwordEncoder.encode(password), name, employeeId, email);
		return employeeRepository.save(employee);
	}

	// Method to find an employee by username for authentication
	public Optional<Employee> findByUsername(String username) {
		return employeeRepository.findByUsername(username);
	}

	// Method to find an employee by ID
	public Optional<Employee> findById(Long id) {
		return employeeRepository.findById(id);
	}
	
	// Method to find an employee by employee ID
	public Optional<Employee> findByEmployeeId(String employeeId) {
		return employeeRepository.findByEmployeeId(employeeId);
	}
	
	// Method to get all employees
	public java.util.List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}
	
	// Method to get employee count
	public long getEmployeeCount() {
		return employeeRepository.count();
	}
	
	@Override // Implement loadUserByUsername from UserDetailsService
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Employee not found with username: " + username));
		
		return new org.springframework.security.core.userdetails.User(
				employee.getUsername(),
				employee.getPasswordHash(),
				new ArrayList<>() // Provide authorities/roles here if applicable
		);
	}




}