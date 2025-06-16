package com.saminavi.attendancePortal.SaminaviAttendanceApplication.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain.Attendance;
import com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain.Employee;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>{

    // Find attendance records for a specific employee and date
    Attendance findByEmployeeAndDate(Employee employee, LocalDate date);
    // Find all attendance records for a specific employee
    List<Attendance> findByEmployee(Employee employee);
    // You can add custom query methods here based on your needs
}
