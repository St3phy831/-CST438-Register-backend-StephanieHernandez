package com.cst438.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;

@ContextConfiguration(classes = { StudentManagementController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class StudentManagementControllerTest {
	static final String URL = "http://localhost:8080";
	public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
	public static final String TEST_STUDENT_NAME  = "test";
	public static final int TEST_STUDENT_NO_HOLD  = 0;
	public static final int TEST_STUDENT_HOLD  = 1;
	
	@MockBean
	StudentRepository studentRepository;
	
	@Autowired
	private MockMvc mvc;
	
	@Test
	public void addStudent()  throws Exception {
		MockHttpServletResponse response;
		
		// should return error if either email or name is not provided or invalid
		Student s = new Student();
		s.setEmail(TEST_STUDENT_EMAIL);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/addStudent")
			      .content(TestUtils.asJsonString(s))
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(400, response.getStatus());
		
		// successfully adding a student
		s.setName(TEST_STUDENT_NAME); // now student obj has name
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/addStudent")
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
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(s);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/addStudent")
			      .content(TestUtils.asJsonString(s))
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(400, response.getStatus());
		
	}
	
	@Test
	public void addHold()  throws Exception {
		MockHttpServletResponse response;
		
		// should return error if email is not provided or invalid
		Student s = new Student();
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .patch("/addHold")
			      .content(TestUtils.asJsonString(s))
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(400, response.getStatus());
		
		// Not adding hold if student already has hold
		s.setEmail(TEST_STUDENT_EMAIL);
		s.setStatusCode(TEST_STUDENT_HOLD);
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(s);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .patch("/addHold")
			      .content(TestUtils.asJsonString(s))
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		// shouldn't be called if already has a hold
		verify(studentRepository, never()).save(any(Student.class));
		
		// successfully adding hold to student without hold
		s.setStatusCode(TEST_STUDENT_NO_HOLD);
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(s);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .patch("/addHold")
			      .content(TestUtils.asJsonString(s))
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		verify(studentRepository).save(any(Student.class));
		
		Student result = TestUtils.fromJsonString(response.getContentAsString(), Student.class);
		assertEquals(TEST_STUDENT_HOLD, result.getStatusCode());
		
		// adding hold to student that doesn't exist
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .patch("/addHold")
			      .content(TestUtils.asJsonString(s))
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(400, response.getStatus());
	}
	
	@Test
	public void removeHold()  throws Exception {
		MockHttpServletResponse response;
		
		// should return error if email is not provided or invalid
		Student s = new Student();
				
		response = mvc.perform(
				MockMvcRequestBuilders
					.patch("/addHold")
					.content(TestUtils.asJsonString(s))
					.characterEncoding("utf-8")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
				
		assertEquals(400, response.getStatus());
		
		// Not removing hold if student doesn't have a hold
		s.setEmail(TEST_STUDENT_EMAIL);
		s.setStatusCode(TEST_STUDENT_NO_HOLD);
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(s);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .patch("/removeHold")
			      .content(TestUtils.asJsonString(s))
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		// shouldn't be called if already doesn't have a hold
		verify(studentRepository, never()).save(any(Student.class));
		
		// successfully removing hold to student with one
		s.setStatusCode(TEST_STUDENT_HOLD);
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(s);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .patch("/removeHold")
			      .content(TestUtils.asJsonString(s))
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		verify(studentRepository).save(any(Student.class));
		
		Student result = TestUtils.fromJsonString(response.getContentAsString(), Student.class);
		assertEquals(TEST_STUDENT_NO_HOLD, result.getStatusCode());
		
		// removing hold to student that doesn't exist
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .patch("/removeHold")
			      .content(TestUtils.asJsonString(s))
			      .characterEncoding("utf-8")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(400, response.getStatus());
	}
}
