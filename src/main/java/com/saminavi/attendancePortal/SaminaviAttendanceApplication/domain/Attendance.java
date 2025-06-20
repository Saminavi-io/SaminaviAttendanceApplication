package com.saminavi.attendancePortal.SaminaviAttendanceApplication.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class Attendance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the primary key
	private long id;
	@ManyToOne // Defines the many-to-one relationship with the Employee entity
	@JoinColumn(name = "employee_id", nullable = false) // Specifies the foreign key column in the 'attendance' table
	@NotNull
	private Employee employee; // The Employee associated with this attendance record
	@Column(nullable = false)
	@NotNull
	private LocalDateTime clockInTime;
	@Column
	private LocalDateTime clockOutTime;
	@Column(nullable = false)
	@NotNull
	private LocalDate date;
	
	// No-args constructor required by JPA
	public Attendance() {
	}
	
	// Constructor for initialization
	public Attendance(Employee employee, LocalDateTime clockInTime, LocalDate date) {
		this.employee = employee;
		this.clockInTime = clockInTime;
		this.date = date;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public LocalDateTime getClockInTime() {
		return clockInTime;
	}
	public void setClockInTime(LocalDateTime clockInTime) {
		this.clockInTime = clockInTime;
	}
	public LocalDateTime getClockOutTime() {
		return clockOutTime;
	}
	public void setClockOutTime(LocalDateTime clockOutTime) {
		this.clockOutTime = clockOutTime;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	
	
	

} 
