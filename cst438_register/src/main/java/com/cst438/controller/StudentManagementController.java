package com.cst438.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;

import java.lang.Object;
import java.util.Optional;

@RestController
// Accept request from these servers/allow call from these
@CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
public class StudentManagementController {
	
	@Autowired
	StudentRepository studentRepository;
	
	@PostMapping("/student")
	@Transactional
	public StudentDTO addStudent( @RequestBody StudentDTO s) { 
		// student must have email and name 
		if (s.email == null || s.email == "" || s.name == null || s.name == "") {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student email and name must be provided/not empty.");
		}
		
		// check student's email is not in use
		Student student = studentRepository.findByEmail(s.email);
		
		if (student == null) {	
			// Create student from StudentDTO
			student = new Student();
			
			student.setEmail(s.email);
			student.setName(s.name);
			student.setStatusCode(s.statusCode);
			student.setStatus(s.status);
			
			studentRepository.save(student);
			
			s.student_id=student.getStudent_id();
			
			return s; // returning dto as confirmation that was saved in database
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student email is already in use: " + s.email);
		}
	}
	
	@PutMapping("/student/{id}")
	@Transactional
	public void updateHold(@PathVariable int id, @RequestParam("status") int status, @RequestParam("msg") String msg) { 
		// check student exists
		Optional<Student> student = studentRepository.findById(id);
			
		if (student.isPresent()) {
			student.get().setStatusCode(status);
			student.get().setStatus(msg);
			studentRepository.save(student.get());
			return;
		} else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student doesn't exist.");
		}
	}
}
