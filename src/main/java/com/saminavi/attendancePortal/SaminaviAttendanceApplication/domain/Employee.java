package com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain;



import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String employeeId;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Attendance> attendanceRecords = new HashSet<>();

    // No-argument constructor (required by JPA)
    public Employee() {
    }

    // Constructor for initialization
    public Employee(String username, String passwordHash, String name, String employeeId) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.employeeId = employeeId;
    }

    // Getters and Setters (Still needed for JPA to access fields)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Set<Attendance> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void setAttendanceRecords(Set<Attendance> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;
    }

    // Helper method to add attendance records
    public void addAttendance(Attendance attendance) {
        this.attendanceRecords.add(attendance);
        attendance.setEmployee(this);
    }

    public void removeAttendance(Attendance attendance) {
        this.attendanceRecords.remove(attendance);
        attendance.setEmployee(null);
    }
}