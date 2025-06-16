package com.saminavi.attendancePortal.SaminaviAttendanceApplication.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain.Attendance;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain.Employee;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.repository.AttendanceRepository;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.repository.EmployeeRepository;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public Attendance clockIn(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        LocalDate today = LocalDate.now();
        Attendance existingAttendance = attendanceRepository.findByEmployeeAndDate(employee, today);
        if (existingAttendance != null) {
            throw new RuntimeException("Employee has already clocked in today");
        }

        Attendance attendance = new Attendance(employee, LocalDateTime.now(), today);
        return attendanceRepository.save(attendance);
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
        return attendanceRepository.save(attendance);
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
}