package com.christy.springboot_masterclass;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import com.christy.springboot_masterclass.StudentRepository;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest // Prepare the application for testing
@AutoConfigureMockMvc
@Transactional  // Changes made during a test are rolled back when the test finishes
class SpringbootMasterclassApplicationTests {

	@Autowired
	private MockMvc mockMvc; // Allows simulation of HTTP requests without starting a real web server
	@Autowired
	private StudentRepository studentRepository; // Spring obtains the repository implementation - interaction with test dB

	@BeforeEach  // Tells JUnit to run this method before every test
	void setUp() {   // Runs first
		studentRepository.deleteAll(); // Clears the student table so that each test starts with a clean database
	}

	@Test // Marks a method as a test that can be run
	void contextLoads() { // Checks whether the Spring application context starts successfully
	}

	@Test
	void getAllStudentsShouldReturnOk() throws Exception { // A simulated GET /students
		Student student = new Student();
		student.name = "Jane";
		student.course = "Java";
		student.level = "Beginner";

		studentRepository.save(student);

		mockMvc.perform(get("/students"))
				.andExpect(status().isOk()) // Checks that request returns HTTP 200 OK
				.andExpect(jsonPath("$.content").exists()) // Inspects specific values in a JSON response
				.andExpect(jsonPath("$.content[0].name").value("Jane")) // The first student's name is Jane
				.andExpect(jsonPath("$.content.length()").value(1)); // length - Counts the items in that array, value[1] - Expects exactly 1 student
				// .andDo(print()) // Debugging line - to print output

	}

	@Test
	void shouldSaveStudent() {
		Student student = new Student(); // Create student object

		student.name = "Jane"; // Assign student's details
		student.course = "Java";
		student.level = "Beginner";

		Student savedStudent = studentRepository.save(student); // Save to db which generates an id

		assertTrue(savedStudent.id > 0);
		assertEquals("Jane", savedStudent.name); // Compares the expected value against the actual value
		assertEquals("Java", savedStudent.course);
		assertEquals("Beginner", savedStudent.level);
	}

	@Test
	void getStudentsShouldRespectPageSize() throws Exception {

		// Page size is 3. Save 4 students to check whether the first page contains only 3 and leaves the fourth for the next page.
		Student student1 = new Student();
		student1.name = "Jane";
		student1.course = "Java";
		student1.level = "Beginner";

		Student student2 = new Student();
		student2.name = "Alex";
		student2.course = "Python";
		student2.level = "Intermediate";

		Student student3 = new Student();
		student3.name = "Mary";
		student3.course = "Spring Boot";
		student3.level = "Beginner";

		Student student4 = new Student();
		student4.name = "Peter";
		student4.course = "Java";
		student4.level = "Advanced";

		studentRepository.save(student1); // Sends student object to the database
		studentRepository.save(student2);
		studentRepository.save(student3);
		studentRepository.save(student4);

		// Request page 0 with a page size of 3
		mockMvc.perform(get("/students")
				.param("page", "0") // Requests the first page
						// .param("page", "1") // Requests the next page
				.param("size", "3")) // Requests 3 students per page
				.andExpect(status().isOk()) // Checks that the request succeeds with HTTP 200
				.andExpect(jsonPath("$.content.length()").value(3)) // Content array to contain exactly 3 student
				//.andExpect(jsonPath("$.content.length()").value(1)) // Content array to contain 1 student
				.andExpect(jsonPath("$.totalElements").value(4));
				//.andExpect(jsonPath("$.content[0].name").value("Peter"));
				//.andDo(print());
	}

	@Test
	void getStudentsShouldSortByNameDescending() throws Exception {

		Student jane = new Student();
		jane.name = "Jane";
		jane.course = "Java";
		jane.level = "Beginner";

		Student alex = new Student();
		alex.name = "Alex";
		alex.course = "Python";
		alex.level = "Intermediate";

		Student mary = new Student();
		mary.name = "Mary";
		mary.course = "Spring Boot";
		mary.level = "Beginner";

		Student peter = new Student();
		peter.name = "Peter";
		peter.course = "Java";
		peter.level = "Advanced";

		studentRepository.saveAll(List.of(jane, alex, mary, peter));

		mockMvc.perform(get("/students")
				.param("sortBy", "name")
				.param("direction", "desc"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value("Peter"))
				.andExpect(jsonPath("$.content[1].name").value("Mary"))
				.andExpect(jsonPath("$.content[2].name").value("Jane"));
				// .andDo(print());

	}

	@Test
	void getStudentsShouldRejectInvalidSortField() throws Exception {

		mockMvc.perform(get("/students")
				.param("sortBy", "age")
				.param("direction", "desc"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid sorting field: age"));
				// .andDo(print());

	}

	@Test
	void getStudentsShouldRejectInvalidSortDirection() throws Exception {

		mockMvc.perform(get("/students")
				.param("sortBy", "name")
				.param("direction", "sideways"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("Direction must be 'asc' or 'desc'!"));
				// .andDo(print());

	}

	@Test
	void shouldRejectStudentWithBlankName() throws Exception {

		Student student = new Student();
		student.name = "";
		student.course = "Java";
		student.level = "Beginner";

		ObjectMapper objectMapper = new ObjectMapper();
		String studentJson = objectMapper.writeValueAsString(student);

		mockMvc.perform(post("/students")
				.contentType(MediaType.APPLICATION_JSON)
				.content(studentJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.name")
						.value("Name cannot be empty!"));
				// .andDo(print());

	}

	@Test
	void shouldRejectStudentWithBlankCourse() throws Exception {

		Student student = new Student();
		student.name = "John";
		student.course = "";
		student.level = "Beginner";

		ObjectMapper objectMapper = new ObjectMapper();
		String studentJson = objectMapper.writeValueAsString(student);

		mockMvc.perform(post("/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content(studentJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.course")
						.value("Course cannot be empty!"));
				// .andDo(print());

	}

	@Test
	void shouldRejectStudentWithBlankLevel() throws Exception {

		Student student = new Student();
		student.name = "John";
		student.course = "Java";
		student.level = "";

		ObjectMapper objectMapper = new ObjectMapper();
		String studentJson = objectMapper.writeValueAsString(student);

		mockMvc.perform(post("/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content(studentJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.level")
						.value("Level cannot be empty!"));
				// .andDo(print());

	}

	@Test
	void shouldCreateStudentWithValidData() throws Exception {

		Student student = new Student();
		student.name = "David";
		student.course = "Spring Boot";
		student.level = "Intermediate";

		ObjectMapper objectMapper = new ObjectMapper();
		String studentJson = objectMapper.writeValueAsString(student);

		mockMvc.perform(post("/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content(studentJson))
				.andExpect(status().isCreated())
				.andExpect(content().string("Student David added!"))
				.andExpect(header().string("Location",
						org.hamcrest.Matchers.matchesPattern("http://localhost/students/\\d+")));
				// .andDo(print());

	}

	@Test
	void getStudentByIdShouldReturnStudent() throws Exception {

		Student student = new Student();
		student.name = "Alice";
		student.course = "Java";
		student.level = "Beginner";

		studentRepository.save(student);

		int studentId = student.id;

		mockMvc.perform(get("/students/" + studentId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Alice"))
				.andExpect(jsonPath("$.course").value("Java"))
				.andExpect(jsonPath("$.level").value("Beginner"));
				//.andDo(print());

	}

	@Test
	void getStudentByIdShouldReturn404WhenStudentDoesNotExist() throws Exception {

		mockMvc.perform(get("/students/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Student 999 not found!"))
				.andExpect(jsonPath("$.status").value(404));
				// .andDo(print());

	}
	@Test
	void getAllStudentsShouldReturnStudentList() throws Exception {

		Student student1 = new Student();
		student1.name = "Alice";
		student1.course = "Java";
		student1.level = "Beginner";

		Student student2 = new Student();
		student2.name = "Brian";
		student2.course = "Python";
		student2.level = "Intermediate";

		studentRepository.save(student1);
		studentRepository.save(student2);

		mockMvc.perform(get("/students"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content.length()").value(2))
				.andExpect(jsonPath("$.content[0].name").value("Alice"))
				.andExpect(jsonPath("$.content[0].course").value("Java"))
				.andExpect(jsonPath("$.content[1].name").value("Brian"))
				.andExpect(jsonPath("$.content[1].course").value("Python"));
				// .andDo(print());

	}

	@Test
	void getAllStudentsShouldReturnEmptyList() throws Exception {

		mockMvc.perform(get("/students"))
				.andExpect(status().isOk()) // HTTP 200
				.andExpect(jsonPath("$.content").isArray()) // The response contains an array
				.andExpect(jsonPath("$.content").isEmpty()) // The array has no students
				.andExpect(jsonPath("$.totalElements").value(0)) // Zero student records
				.andExpect(jsonPath("$.totalPages").value(0)); // Zero pages of results
				// .andDo(print());

	}

	@Test
	void deleteStudentShouldRemoveStudent() throws Exception {

		Student student = new Student();
		student.name = "Charles";
		student.course = "Spring Boot";
		student.level = "Intermediate"; // Create an existing student before deletion

		studentRepository.save(student);

		int studentId = student.id;

		mockMvc.perform(delete("/students/" + studentId))
				.andExpect(status().isOk());

		assertFalse(studentRepository.existsById(studentId));

	}

	@Test
	void deleteStudentShouldReturn404WhenStudentDoesNotExist() throws Exception {

		mockMvc.perform(delete("/students/999"))  // Attempts to delete student ID 999
				.andExpect(status().isNotFound()) // Expects HTTP 404
				.andExpect(jsonPath("$.message").value("Student 999 not found!")) // Verifies the exception message
				.andExpect(jsonPath("$.status").value(404)); // Verifies the status in the JSON response
				// .andDo(print());

	}

	@Test
	void updateStudentShouldUpdateAllFields() throws Exception {

		Student student = new Student();
		student.name = "Grace";
		student.course = "Java";
		student.level = "Beginner";

		studentRepository.save(student);

		int studentId = student.id; // Capture her generated ID so we can identify her in the PUT request

		StudentUpdateDTO updatedStudent = new StudentUpdateDTO();
		updatedStudent.name = "Grace Wanjiku";
		updatedStudent.course = "Spring Boot";
		updatedStudent.level = "Intermediate";

		ObjectMapper objectMapper = new ObjectMapper(); // Converts our StudentUpdateDTO object into JSON
		String updatedStudentJson = objectMapper.writeValueAsString(updatedStudent); // Produces the JSON request body that MockMvc will send to the API

		mockMvc.perform(put("/students/" + studentId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updatedStudentJson))
				.andExpect(status().isOk())
				.andExpect(content().string("Student " + studentId + " updated!"));
				// .andDo(print());

		Student updated = studentRepository.findById(studentId).orElseThrow(); // Retrieves the student from the database using the same ID

		assertEquals("Grace Wanjiku", updated.name);

		assertEquals("Spring Boot", updated.course);
		assertEquals("Intermediate", updated.level);

	}

	@Test
	void updateNonExistentStudentShouldReturn404() throws Exception {

		StudentUpdateDTO updatedStudent = new StudentUpdateDTO();
		updatedStudent.name = "Grace Wanjiku";
		updatedStudent.course = "Spring Boot";
		updatedStudent.level = "Intermediate";

		ObjectMapper objectMapper = new ObjectMapper();
		String updatedStudentJson = objectMapper.writeValueAsString(updatedStudent);

		mockMvc.perform(put("/students/999")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updatedStudentJson))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Student 999 not found!"))
				.andExpect(jsonPath("$.status").value(404));
				// .andDo(print());

	}

	@Test
	void patchStudentShouldUpdateOnlyProvidedFields() throws Exception {

		Student student = new Student();
		student.name = "Grace";
		student.course = "Java";
		student.level = "Beginner";

		studentRepository.save(student);

		int studentId = student.id;

		StudentPatchDTO patchData = new StudentPatchDTO();
		patchData.name = "Grace Wanjiku";
		patchData.level = "Intermediate";

		ObjectMapper objectMapper = new ObjectMapper();
		String patchJson = objectMapper.writeValueAsString(patchData);

		mockMvc.perform(patch("/students/" + studentId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(patchJson))
				.andExpect(status().isOk())
				.andExpect(content().string("Student " + studentId + " updated!"));
				// .andDo(print());

		Student updated = studentRepository.findById(studentId).orElseThrow();

		assertEquals("Grace Wanjiku", updated.name);
		assertEquals("Intermediate", updated.level);
		assertEquals("Java", updated.course);

	}

	@Test
	void patchStudentWithBlankNameShouldReturn400() throws Exception {

		Student student = new Student();
		student.name = "Grace";
		student.course = "Java";
		student.level = "Beginner";

		studentRepository.save(student);

		int studentId = student.id;

		StudentPatchDTO patchData = new StudentPatchDTO();
		patchData.name = " ";

		ObjectMapper objectMapper = new ObjectMapper();
		String patchJson = objectMapper.writeValueAsString(patchData);

		mockMvc.perform(patch("/students/" + studentId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(patchJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Name cannot be blank!"))
				.andExpect(jsonPath("$.errors").value(org.hamcrest.Matchers.nullValue()));
				// .andDo(print());

	}

	@Test
	void patchStudentWithBlankCourseShouldReturn400() throws Exception {

		Student student = new Student();
		student.name = "Grace";
		student.course = "Java";
		student.level = "Beginner";

		studentRepository.save(student);

		int studentId = student.id;

		StudentPatchDTO patchData = new StudentPatchDTO();
		patchData.course = " ";

		ObjectMapper objectMapper = new ObjectMapper();
		String patchJson = objectMapper.writeValueAsString(patchData);

		mockMvc.perform(patch("/students/" + studentId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(patchJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Course cannot be blank!"))
				.andExpect(jsonPath("$.errors").value(org.hamcrest.Matchers.nullValue()));
				// .andDo(print());

	}
	@Test
	void patchStudentWithBlankLevelShouldReturn400() throws Exception {

		Student student = new Student();
		student.name = "Grace";
		student.course = "Java";
		student.level = "Beginner";

		studentRepository.save(student);

		int studentId = student.id;

		StudentPatchDTO patchData = new StudentPatchDTO();
		patchData.level = " ";

		ObjectMapper objectMapper = new ObjectMapper();
		String patchJson = objectMapper.writeValueAsString(patchData);

		mockMvc.perform(patch("/students/" + studentId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(patchJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Level cannot be blank!"))
				.andExpect(jsonPath("$.errors").value(org.hamcrest.Matchers.nullValue()));
				// .andDo(print());

	}

	@Test
	void patchNonExistentStudentShouldReturn404() throws Exception {

		StudentPatchDTO patchData = new StudentPatchDTO();
		patchData.name = "Grace Wanjiku";
		patchData.level = "Intermediate";

		ObjectMapper objectMapper = new ObjectMapper();
		String patchJson = objectMapper.writeValueAsString(patchData);

		mockMvc.perform(patch("/students/999")
				.contentType(MediaType.APPLICATION_JSON)
				.content(patchJson))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Student 999 not found!"))
				.andExpect(jsonPath("$.status").value(404))
				.andDo(print());

	}


}