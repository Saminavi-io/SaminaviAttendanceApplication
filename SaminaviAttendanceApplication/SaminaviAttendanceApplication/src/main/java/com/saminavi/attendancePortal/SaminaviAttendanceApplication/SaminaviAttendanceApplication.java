package com.saminavi.attendancePortal.SaminaviAttendanceApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SaminaviAttendanceApplication {

	private static final Logger logger = LoggerFactory.getLogger(SaminaviAttendanceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Saminavi Attendance Application...");
		SpringApplication.run(SaminaviAttendanceApplication.class, args);
		logger.info("Saminavi Attendance Application started successfully!");
	}

}
