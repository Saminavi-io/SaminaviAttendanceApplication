package com.saminavi.attendancePortal.SaminaviAttendanceApplication.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain.Attendance;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain.Employee;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EmailService emailService;

    @Value("${notification.hr.email:hr@saminavi.com}")
    private String hrEmail;

    @Value("${notification.manager.email:manager@saminavi.com}")
    private String managerEmail;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void notifyClockIn(Employee employee, Attendance attendance) {
        try {
            // Send notification to HR
            sendHRClockInNotification(employee, attendance);
            
            // Send notification to Manager
            sendManagerClockInNotification(employee, attendance);
            
            logger.info("Clock in notifications sent for employee: {}", employee.getUsername());
        } catch (Exception e) {
            logger.error("Failed to send clock in notifications for employee: {}", employee.getUsername(), e);
        }
    }

    public void notifyClockOut(Employee employee, Attendance attendance) {
        try {
            // Send notification to HR
            sendHRClockOutNotification(employee, attendance);
            
            // Send notification to Manager
            sendManagerClockOutNotification(employee, attendance);
            
            logger.info("Clock out notifications sent for employee: {}", employee.getUsername());
        } catch (Exception e) {
            logger.error("Failed to send clock out notifications for employee: {}", employee.getUsername(), e);
        }
    }

    private void sendHRClockInNotification(Employee employee, Attendance attendance) {
        String subject = "Employee Clock In - " + employee.getName();
        String message = String.format(
            "Dear HR Team,\n\n" +
            "An employee has clocked in.\n\n" +
            "Employee Details:\n" +
            "- Name: %s\n" +
            "- Employee ID: %s\n" +
            "- Username: %s\n" +
            "- Date: %s\n" +
            "- Clock In Time: %s\n\n" +
            "Best regards,\n" +
            "Attendance System",
            employee.getName(),
            employee.getEmployeeId(),
            employee.getUsername(),
            attendance.getDate().format(DATE_FORMATTER),
            attendance.getClockInTime().format(TIME_FORMATTER)
        );
        
        emailService.sendEmailAsync(hrEmail, subject, message)
            .thenAccept(success -> {
                if (success) {
                    logger.info("HR clock in notification sent successfully for employee: {}", employee.getUsername());
                } else {
                    logger.error("Failed to send HR clock in notification for employee: {}", employee.getUsername());
                }
            });
    }

    private void sendHRClockOutNotification(Employee employee, Attendance attendance) {
        String subject = "Employee Clock Out - " + employee.getName();
        String message = String.format(
            "Dear HR Team,\n\n" +
            "An employee has clocked out.\n\n" +
            "Employee Details:\n" +
            "- Name: %s\n" +
            "- Employee ID: %s\n" +
            "- Username: %s\n" +
            "- Date: %s\n" +
            "- Clock In Time: %s\n" +
            "- Clock Out Time: %s\n" +
            "- Total Hours: %s\n\n" +
            "Best regards,\n" +
            "Attendance System",
            employee.getName(),
            employee.getEmployeeId(),
            employee.getUsername(),
            attendance.getDate().format(DATE_FORMATTER),
            attendance.getClockInTime().format(TIME_FORMATTER),
            attendance.getClockOutTime().format(TIME_FORMATTER),
            calculateWorkHours(attendance)
        );
        
        emailService.sendEmailAsync(hrEmail, subject, message)
            .thenAccept(success -> {
                if (success) {
                    logger.info("HR clock out notification sent successfully for employee: {}", employee.getUsername());
                } else {
                    logger.error("Failed to send HR clock out notification for employee: {}", employee.getUsername());
                }
            });
    }

    private void sendManagerClockInNotification(Employee employee, Attendance attendance) {
        String subject = "Team Member Clock In - " + employee.getName();
        String message = String.format(
            "Dear Manager,\n\n" +
            "A team member has clocked in.\n\n" +
            "Employee Details:\n" +
            "- Name: %s\n" +
            "- Employee ID: %s\n" +
            "- Date: %s\n" +
            "- Clock In Time: %s\n\n" +
            "Best regards,\n" +
            "Attendance System",
            employee.getName(),
            employee.getEmployeeId(),
            attendance.getDate().format(DATE_FORMATTER),
            attendance.getClockInTime().format(TIME_FORMATTER)
        );
        
        emailService.sendEmailAsync(managerEmail, subject, message)
            .thenAccept(success -> {
                if (success) {
                    logger.info("Manager clock in notification sent successfully for employee: {}", employee.getUsername());
                } else {
                    logger.error("Failed to send manager clock in notification for employee: {}", employee.getUsername());
                }
            });
    }

    private void sendManagerClockOutNotification(Employee employee, Attendance attendance) {
        String subject = "Team Member Clock Out - " + employee.getName();
        String message = String.format(
            "Dear Manager,\n\n" +
            "A team member has clocked out.\n\n" +
            "Employee Details:\n" +
            "- Name: %s\n" +
            "- Employee ID: %s\n" +
            "- Date: %s\n" +
            "- Clock In Time: %s\n" +
            "- Clock Out Time: %s\n" +
            "- Total Hours: %s\n\n" +
            "Best regards,\n" +
            "Attendance System",
            employee.getName(),
            employee.getEmployeeId(),
            attendance.getDate().format(DATE_FORMATTER),
            attendance.getClockInTime().format(TIME_FORMATTER),
            attendance.getClockOutTime().format(TIME_FORMATTER),
            calculateWorkHours(attendance)
        );
        
        emailService.sendEmailAsync(managerEmail, subject, message)
            .thenAccept(success -> {
                if (success) {
                    logger.info("Manager clock out notification sent successfully for employee: {}", employee.getUsername());
                } else {
                    logger.error("Failed to send manager clock out notification for employee: {}", employee.getUsername());
                }
            });
    }

    private String calculateWorkHours(Attendance attendance) {
        if (attendance.getClockOutTime() == null) {
            return "N/A";
        }
        
        long hours = java.time.Duration.between(attendance.getClockInTime(), attendance.getClockOutTime()).toHours();
        long minutes = java.time.Duration.between(attendance.getClockInTime(), attendance.getClockOutTime()).toMinutesPart();
        
        return String.format("%d hours %d minutes", hours, minutes);
    }
} 