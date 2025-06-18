package com.saminavi.attendancePortal.SaminaviAttendanceApplication.controller;

import com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain.Attendance;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance") // Base path for attendance endpoints
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/clock-in") // Handles clock-in requests
    public ResponseEntity<String> clockIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try {
            attendanceService.clockIn(username);
            return new ResponseEntity<>("Clock-in successful", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/clock-out") // Handles clock-out requests
    public ResponseEntity<String> clockOut() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try {
            attendanceService.clockOut(username);
            return new ResponseEntity<>("Clock-out successful", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/today") // Handles requests to get today's attendance
    public ResponseEntity<?> getTodayAttendance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Attendance todayAttendance = attendanceService.getTodayAttendance(username);
        if (todayAttendance == null) {
            return new ResponseEntity<>("No attendance recorded for today", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(todayAttendance, HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getAttendanceHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        java.util.List<Attendance> history = attendanceService.getAttendanceHistory(username);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    @GetMapping("/all-today")
    public ResponseEntity<?> getAllTodayAttendance() {
        java.util.List<Attendance> allTodayAttendance = attendanceService.getAllTodayAttendance();
        return new ResponseEntity<>(allTodayAttendance, HttpStatus.OK);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeAttendance(@PathVariable String employeeId) {
        java.util.List<Attendance> employeeAttendance = attendanceService.getEmployeeAttendance(employeeId);
        return new ResponseEntity<>(employeeAttendance, HttpStatus.OK);
    }
}