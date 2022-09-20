package com.cst438.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.domain.StudentRepository;
import com.cst438.test.utils.TestUtils;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;

@ContextConfiguration(classes = { StudentManagementController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class StudentManagementControllerTest {
	static final String URL = "http://localhost:8080";
	public static final int TEST_STUDENT_ID = 1;
	public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
	public static final String TEST_STUDENT_NAME  = "test";
	
	@MockBean
	StudentRepository studentRepository;
	
	@Autowired
	private MockMvc mvc;
	
	@Test
	public void addStudent()  throws Exception {
		MockHttpServletResponse response;
		
		// should return error if either email or name is not provided or invalid
		StudentDTO s = new StudentDTO(TEST_STUDENT_EMAIL, "");
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .content(TestUtils.asJsonString(s))
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(400, response.getStatus());
		
		// successfully adding a student
		s = new StudentDTO(TEST_STUDENT_EMAIL, TEST_STUDENT_NAME); // now student obj has name
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .content(TestUtils.asJsonString(s))
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		verify(studentRepository).save(any(Student.class));
				
		Student result = TestUtils.fromJsonString(response.getContentAsString(), Student.class);
		assertEquals(TEST_STUDENT_EMAIL, result.getEmail());
		assertEquals(TEST_STUDENT_NAME, result.getName());
		
		// adding a student with existing email
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(new Student());
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .content(TestUtils.asJsonString(s))
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(400, response.getStatus());
		
	}
	
	@Test
	public void updateHold()  throws Exception {
		MockHttpServletResponse response;
		
		// should return error if student doesn't exist
		given(studentRepository.findById(TEST_STUDENT_ID)).willReturn(Optional.empty());
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .put("/student/1?status=0&msg=")
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		 assertEquals(400, response.getStatus());
		
		// successfully updating student hold
		Student s = new Student();
		s.setEmail(TEST_STUDENT_EMAIL);
		s.setName(TEST_STUDENT_NAME);
		
		given(studentRepository.findById(TEST_STUDENT_ID)).willReturn(Optional.of(s));
		
		response = mvc.perform(
				MockMvcRequestBuilders
				  .put("/student/1?status=0&msg=")
				  .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		verify(studentRepository).save(any(Student.class));
	}
}
