package com.saminavi.attendancePortal.SaminaviAttendanceApplication.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional
	public Employee registerEmployee(String username, String password, String name, String email) {
		// Check if username exists
		if (employeeRepository.findByUsername(username).isPresent()) {
			throw new RuntimeException("Username already exists");
		}
		// Check if email exists
		if (employeeRepository.findByEmail(email).isPresent()) {
			throw new RuntimeException("Email already exists");
		}
		// Generate next employeeId with EMP prefix
		List<Employee> allEmployees = employeeRepository.findAll();
		int maxNum = 0;
		boolean foundValid = false;
		for (Employee emp : allEmployees) {
			String empId = emp.getEmployeeId();
			if (empId != null && empId.startsWith("EMP")) {
				try {
					int num = Integer.parseInt(empId.substring(3));
					if (num > maxNum) maxNum = num;
					foundValid = true;
				} catch (NumberFormatException ignored) {}
			}
		}
		// If no valid EMP-prefixed IDs, start with EMP001
		String nextEmployeeId = String.format("EMP%03d", foundValid ? (maxNum + 1) : 1);
		Employee employee = new Employee(username, passwordEncoder.encode(password), name, nextEmployeeId, email);
		employee = employeeRepository.saveAndFlush(employee); // Ensure immediate commit
		return employee;
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