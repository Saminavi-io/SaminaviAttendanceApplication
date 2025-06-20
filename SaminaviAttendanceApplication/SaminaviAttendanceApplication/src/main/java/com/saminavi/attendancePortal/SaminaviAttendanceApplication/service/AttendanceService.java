package com.saminavi.attendancePortal.SaminaviAttendanceApplication.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain.Attendance;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain.Employee;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.repository.AttendanceRepository;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.repository.EmployeeRepository;

@Service
public class AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public AttendanceService(AttendanceRepository attendanceRepository, 
                           EmployeeRepository employeeRepository,
                           EmailService emailService,
                           NotificationService notificationService) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Attendance clockIn(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        LocalDate today = LocalDate.now();
        System.out.println("[CLOCK IN DEBUG] Attempting clock-in for employee: " + employee.getUsername() + ", ID: " + employee.getEmployeeId() + ", Date: " + today);
        Attendance existingAttendance = attendanceRepository.findByEmployeeAndDate(employee, today);
        if (existingAttendance != null) {
            System.out.println("[CLOCK IN DEBUG] Existing attendance found for employee: " + employee.getUsername() + ", Date: " + today);
            throw new RuntimeException("Employee has already clocked in today");
        }

        Attendance attendance = new Attendance(employee, LocalDateTime.now(), today);
        Attendance savedAttendance = attendanceRepository.save(attendance);
        
        // Send email notification
        sendClockInEmail(employee, savedAttendance);
        
        // Send organizational notification
        notificationService.notifyClockIn(employee, savedAttendance);
        
        return savedAttendance;
    }

    @Transactional
    public Attendance clockOut(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee, today);

        if (attendance == null || attendance.getClockOutTime() != null) {
            throw new RuntimeException("Cannot clock out. Employee is not clocked in or already clocked out today");
        }

        attendance.setClockOutTime(LocalDateTime.now());
        Attendance savedAttendance = attendanceRepository.save(attendance);
        
        // Send email notification
        sendClockOutEmail(employee, savedAttendance);
        
        // Send organizational notification
        notificationService.notifyClockOut(employee, savedAttendance);
        
        return savedAttendance;
    }

    private void sendClockInEmail(Employee employee, Attendance attendance) {
        try {
            String subject = "Clock In Confirmation - " + employee.getName();
            String message = String.format(
                "Dear %s,\n\n" +
                "Your clock in has been recorded successfully.\n\n" +
                "Details:\n" +
                "- Employee ID: %s\n" +
                "- Date: %s\n" +
                "- Clock In Time: %s\n\n" +
                "Thank you for your dedication!\n\n" +
                "Best regards,\n" +
                "Saminavi Organization",
                employee.getName(),
                employee.getEmployeeId(),
                attendance.getDate().format(DATE_FORMATTER),
                attendance.getClockInTime().format(TIME_FORMATTER)
            );
            
            // Send email asynchronously for better concurrent handling
            emailService.sendEmailAsync(employee.getEmail(), subject, message)
                .thenAccept(success -> {
                    if (success) {
                        logger.info("Clock in email sent successfully to employee: {}", employee.getUsername());
                    } else {
                        logger.error("Failed to send clock in email to employee: {}", employee.getUsername());
                    }
                });
        } catch (Exception e) {
            logger.error("Failed to send clock in email to employee: {}", employee.getUsername(), e);
        }
    }

    private void sendClockOutEmail(Employee employee, Attendance attendance) {
        try {
            String subject = "Clock Out Confirmation - " + employee.getName();
            String message = String.format(
                "Dear %s,\n\n" +
                "Your clock out has been recorded successfully.\n\n" +
                "Details:\n" +
                "- Employee ID: %s\n" +
                "- Date: %s\n" +
                "- Clock In Time: %s\n" +
                "- Clock Out Time: %s\n" +
                "- Total Hours: %s\n\n" +
                "Thank you for your hard work today!\n\n" +
                "Best regards,\n" +
                "Saminavi Organization",
                employee.getName(),
                employee.getEmployeeId(),
                attendance.getDate().format(DATE_FORMATTER),
                attendance.getClockInTime().format(TIME_FORMATTER),
                attendance.getClockOutTime().format(TIME_FORMATTER),
                calculateWorkHours(attendance)
            );
            
            // Send email asynchronously for better concurrent handling
            emailService.sendEmailAsync(employee.getEmail(), subject, message)
                .thenAccept(success -> {
                    if (success) {
                        logger.info("Clock out email sent successfully to employee: {}", employee.getUsername());
                    } else {
                        logger.error("Failed to send clock out email to employee: {}", employee.getUsername());
                    }
                });
        } catch (Exception e) {
            logger.error("Failed to send clock out email to employee: {}", employee.getUsername(), e);
        }
    }

    private String calculateWorkHours(Attendance attendance) {
        if (attendance.getClockOutTime() == null) {
            return "N/A";
        }
        
        long hours = java.time.Duration.between(attendance.getClockInTime(), attendance.getClockOutTime()).toHours();
        long minutes = java.time.Duration.between(attendance.getClockInTime(), attendance.getClockOutTime()).toMinutesPart();
        
        return String.format("%d hours %d minutes", hours, minutes);
    }

    public Attendance getTodayAttendance(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        LocalDate today = LocalDate.now();
        return attendanceRepository.findByEmployeeAndDate(employee, today);
    }

    public List<Attendance> getAttendanceHistory(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return attendanceRepository.findByEmployee(employee);
    }

    public List<Attendance> getAllTodayAttendance() {
        LocalDate today = LocalDate.now();
        return attendanceRepository.findByDate(today);
    }

    public List<Attendance> getEmployeeAttendance(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        return attendanceRepository.findByEmployee(employee);
    }
}