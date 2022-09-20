package com.cst438.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
public class StudentManagementController {
	public static final int NO_HOLD  = 0;
	public static final int HOLD  = 1;
	
	@Autowired
	StudentRepository studentRepository;
	
	@PostMapping("/addStudent")
	@Transactional
	public Student addStudent( @RequestBody Student s) { 
		// student must have email and name 
		if (s.getEmail() == null || s.getEmail() == "" || s.getName() == null || s.getName() == "") {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student email and name must be provided/not empty.");
		}
		
		// check student's email is not in use
		Student student = studentRepository.findByEmail(s.getEmail());
		
		if (student == null) {		
			studentRepository.save(s);
			return s; // returning s as confirmation that was saved in database
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student email is already in use: " + s.getEmail());
		}
	}
	
	@PatchMapping("/addHold")
	@Transactional
	public Student addHold( @RequestBody Student s) { 
		// must provide student email at least
		if (s.getEmail() == null || s.getEmail() == "") {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student email must be provided/not empty.");
		}
				
		// checks student exist
		Student student = studentRepository.findByEmail(s.getEmail());
			
		if (student != null) {
			if(student.getStatusCode() == 0) { // checks doesn't have a hold
				student.setStatusCode(HOLD);			
				studentRepository.save(student);
			}
			return student;
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student doesn't exist.");
		}
		
	}
	
	@PatchMapping("/removeHold")
	@Transactional
	public Student removeHold( @RequestBody Student s) { 
		// must provide student email at least
		if (s.getEmail() == null || s.getEmail() == "") {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student email must be provided/not empty.");
		}

		// checks student exist
		Student student = studentRepository.findByEmail(s.getEmail());
			
		if (student != null) {
			if(student.getStatusCode() != 0) { // checks whether has a hold
				student.setStatusCode(NO_HOLD);			
				studentRepository.save(student);
			}
			return student;
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student doesn't exist.");
		}
		
	}
	
}
