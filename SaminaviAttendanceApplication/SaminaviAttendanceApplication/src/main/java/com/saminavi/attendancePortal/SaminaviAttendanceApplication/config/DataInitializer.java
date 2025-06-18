package com.saminavi.attendancePortal.SaminaviAttendanceApplication.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain.Employee;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.repository.EmployeeRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if employees already exist
        if (employeeRepository.count() == 0) {
            logger.info("Initializing sample employees...");
            
            // Create sample employees (all emails set to sridhar.uppu@saminavi.io for testing)
            createEmployee("krishna.g", "password123", "Krishna G", "EMP007", "sridhar.uppu@saminavi.io");
            createEmployee("jane.smith", "password123", "Jane Smith", "EMP005", "sridhar.uppu@saminavi.io");
            createEmployee("ajay.g", "password123", "Ajay G", "EMP004", "sridhar.uppu@saminavi.io");
            
            logger.info("Sample employees created successfully!");
        } else {
            logger.info("Employees already exist, skipping initialization.");
            
            // Optionally add more employees if they don't exist
            // Uncomment the lines below to add more employees
            // createEmployee("john.doe", "password123", "John Doe", "EMP006", "john.doe@saminavi.io");
            // createEmployee("sarah.wilson", "password123", "Sarah Wilson", "EMP007", "sarah.wilson@saminavi.io");
        }
    }

    private void createEmployee(String username, String password, String name, String employeeId, String email) {
        try {
            // Check if employee already exists
            if (employeeRepository.findByUsername(username).isPresent()) {
                logger.info("Employee with username {} already exists, skipping.", username);
                return;
            }

            if (employeeRepository.findByEmployeeId(employeeId).isPresent()) {
                logger.info("Employee with ID {} already exists, skipping.", employeeId);
                return;
            }

            if (employeeRepository.findByEmail(email).isPresent()) {
                logger.info("Employee with email {} already exists, skipping.", email);
                return;
            }

            // Create new employee
            Employee employee = new Employee(
                username,
                passwordEncoder.encode(password),
                name,
                employeeId,
                email
            );

            employeeRepository.save(employee);
            logger.info("Created employee: {} ({})", name, employeeId);
            
        } catch (Exception e) {
            logger.error("Failed to create employee: {}", username, e);
        }
    }
} 