package com.saminavi.attendancePortal.SaminaviAttendanceApplication.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByEmployeeId(String employeeId);
    Optional<Employee> findByEmail(String email);

    // Find the max numeric employeeId (as string)
    @Query("SELECT MAX(CAST(e.employeeId AS long)) FROM Employee e")
    Long findMaxEmployeeIdAsLong();
}
