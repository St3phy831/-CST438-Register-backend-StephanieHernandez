package com.cst438;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

@SpringBootTest
public class EndToEndAddStudentTest {
	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/stephaniehernandez/Downloads/chromedriver";
	public static final String URL = "http://localhost:3000/admin";
	public static final String ADD_MSG = "Student successfully added";
	public static final String CANT_ADD_MSG = "Can't add student";
	public static final String TEST_STUDENT_ENROLLED_EMAIL = "addTest@csumb.edu";
	public static final String TEST_STUDENT_ENROLLED_NAME = "add_test";
	public static final String TEST_STUDENT_NOT_ENROLLED_EMAIL = "addTest2@csumb.edu";
	public static final String TEST_STUDENT_NOT_ENROLLED_NAME = "add_test2";
	public static final String STATUS = "";
	public static final int STATUS_CODE = 0;
	public static final int SLEEP_DURATION = 2000; // 2 seconds

	@Autowired
	StudentRepository studentRepository;

	@Test
	public void addExistingStudent() throws Exception {
		// Make sure test is enrolled
		Student s = studentRepository.findByEmail(TEST_STUDENT_ENROLLED_EMAIL);
		if (s == null) {
			s = new Student();
			s.setEmail(TEST_STUDENT_ENROLLED_EMAIL);
			s.setName(TEST_STUDENT_ENROLLED_NAME);
			s.setStatusCode(STATUS_CODE);
			s.setStatus(STATUS);
			studentRepository.save(s);
		}

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {

			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// click Add Student button to display pop up window
			driver.findElement(By.id("AddStudent")).click();
			Thread.sleep(SLEEP_DURATION);

			// enter student info and click Add button
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_STUDENT_ENROLLED_NAME);
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_STUDENT_ENROLLED_EMAIL);
			driver.findElement(By.id("Add")).click();
			Thread.sleep(SLEEP_DURATION);

			String toast_text = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			// Make sure correct message is displayed
			assertEquals(CANT_ADD_MSG, toast_text);
			Thread.sleep(SLEEP_DURATION);
		} catch (Exception ex) {
			throw ex;
		} finally {
			// Make sure test is deleted (for db cleanup)
			s = studentRepository.findByEmail(TEST_STUDENT_ENROLLED_EMAIL);
			if (s != null) {
				studentRepository.delete(s);
			}
			driver.quit();
		}
	}

	@Test
	public void addStudent() throws Exception {
		// Make sure test2 is not enrolled
		Student s = studentRepository.findByEmail(TEST_STUDENT_NOT_ENROLLED_EMAIL);
		if (s != null) {
			studentRepository.delete(s);
		}

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {

			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// click Add Student button to display pop up window
			driver.findElement(By.id("AddStudent")).click();
			Thread.sleep(SLEEP_DURATION);

			// enter student info and click Add button
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_STUDENT_NOT_ENROLLED_NAME);
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_STUDENT_NOT_ENROLLED_EMAIL);
			driver.findElement(By.id("Add")).click();
			Thread.sleep(SLEEP_DURATION);

			String toast_text = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			// Make sure correct message is displayed
			assertEquals(ADD_MSG, toast_text);

			// verify that student was inserted to database
			s = studentRepository.findByEmail(TEST_STUDENT_NOT_ENROLLED_EMAIL);
			assertNotNull(s, "Student was not found in database.");
			Thread.sleep(SLEEP_DURATION);
		} catch (Exception ex) {
			throw ex;
		} finally {
			// Make sure test2 is deleted (for db cleanup)
			s = studentRepository.findByEmail(TEST_STUDENT_NOT_ENROLLED_EMAIL);
			if (s != null) {
				studentRepository.delete(s);
			}
			driver.quit();
		}
	}
}
