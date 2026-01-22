package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Commit;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Commit
class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    TestRestTemplate testRestTemplate;

    private Student testStudent;
    private String mainUrl;

    Long testStudentId;

    @BeforeEach
    void setUp() {
        mainUrl = "http://localhost:" + port + "/student";
        studentRepository.deleteAll();
        testStudent = new Student();
        testStudent.setName("Harry Potter");
        testStudent.setAge(11);
        testStudent = studentRepository.save(testStudent);
        testStudentId = testStudent.getId();
    }

    @Test
    public void getStudentTest() throws Exception {
        assertNotNull(testStudentId);
        ResponseEntity<Student> response = testRestTemplate.getForEntity(mainUrl + "/{id}", Student.class, testStudentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testStudentId, response.getBody().getId());
        assertEquals("Harry Potter", response.getBody().getName());
        assertEquals(11, response.getBody().getAge());
    }

    @Test
    public void getStudent_ByNonExistingId_Test() {
        Long nonExistingId = 999999L;

        ResponseEntity<Student> response = testRestTemplate.getForEntity(
                mainUrl + "/{id}", Student.class, nonExistingId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createStudentTest() {
        Student newStudent = new Student();
        newStudent.setName("Hermione Granger");
        newStudent.setAge(11);
        newStudent = studentRepository.save(newStudent);


        ResponseEntity<Student> response = testRestTemplate.postForEntity(
                mainUrl, newStudent, Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Hermione Granger", response.getBody().getName());
    }

    @Test
    public void changeStudentTest() throws Exception {
        int anotherAge = 12;
        Student updatedStudent = new Student();
        updatedStudent.setName(testStudent.getName());
        updatedStudent.setAge(anotherAge);
        updatedStudent = studentRepository.save(updatedStudent);

        ResponseEntity<Student> response = testRestTemplate.exchange(
                "/student/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updatedStudent),
                Student.class,
                testStudentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Student updatedData = response.getBody();
        assertThat(updatedData).isNotNull();
        assertThat(updatedData.getId()).isEqualTo(testStudentId);
        assertThat(updatedData.getAge()).isEqualTo(anotherAge);
        assertThat(updatedData.getName()).isEqualTo(testStudent.getName());
    }

    @Test
    public void deleteStudentTest() throws Exception {

        testRestTemplate.delete("/student/{id}", testStudentId);
        ResponseEntity<Student> response = testRestTemplate.getForEntity(mainUrl + "/{id}",
                Student.class, testStudentId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getAllStudentsTest() throws Exception {
        Student newStudent = studentRepository.save(new Student("Ron Weasley", 12));
        Long newStudentId = newStudent.getId();

        assertNotNull(newStudentId);

        ResponseEntity<Collection<Student>> response = testRestTemplate.exchange(mainUrl, HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Collection<Student> students = response.getBody();
        assertThat(students).isNotNull();
        assertThat(students).hasSize(2);

        List<String> allStudentsNames = students.stream()
                .map(Student::getName)
                .toList();

        assertThat(allStudentsNames).containsExactlyInAnyOrder("Ron Weasley", "Harry Potter");
    }
}

