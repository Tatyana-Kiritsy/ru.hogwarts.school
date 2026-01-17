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
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Commit
class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    FacultyRepository facultyRepository;

    private Faculty testFaculty;
    private String mainUrl;
    private Long testFacultyId;

    @BeforeEach
    void setUp() {
        mainUrl = "http://localhost:" + port + "/faculty";
        facultyRepository.deleteAll();
        testFaculty = facultyRepository.save(new Faculty("Gryffindor", "green"));
        testFacultyId = testFaculty.getId();
    }

    @Test
    void getFacultyTest() throws Exception {
        assertNotNull(testFacultyId);
        ResponseEntity<Faculty> response = testRestTemplate.getForEntity(mainUrl + "/{id}",
                Faculty.class, testFacultyId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(testFacultyId);
        assertThat(response.getBody().getName()).isEqualTo("Gryffindor");
        assertThat(response.getBody().getColor()).isEqualTo("green");
    }

    @Test
    void getFaculty_ByNonExistingId_Test() throws Exception {
        Long nonExistingId = 999999L;

        ResponseEntity<Faculty> response = testRestTemplate.getForEntity(mainUrl + "/{id}",
                Faculty.class, nonExistingId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void addFacultyTest() throws Exception {
        Faculty newFaculty = new Faculty("Slytherin", "silver");
        newFaculty = facultyRepository.save(newFaculty);

        ResponseEntity<Faculty> response = testRestTemplate.postForEntity(mainUrl, newFaculty, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Slytherin");
        assertThat(response.getBody().getColor()).isEqualTo("silver");
    }

    @Test
    void changeFacultyTest() throws Exception {
        String anotherColor = "red";
        Faculty updatedFaculty = new Faculty(testFaculty.getName(), anotherColor);
        updatedFaculty = facultyRepository.save(updatedFaculty);

        ResponseEntity<Faculty> response = testRestTemplate.exchange("/faculty/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updatedFaculty),
                Faculty.class,
                testFacultyId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(testFacultyId);
        assertThat(response.getBody().getName()).isEqualTo(testFaculty.getName());
        assertThat(response.getBody().getColor()).isEqualTo("red");
    }

    @Test
    void deleteFacultyTest() throws Exception {
        assertThat(testFacultyId).isNotNull();
        testRestTemplate.delete("/faculty/{id}", testFacultyId);

        ResponseEntity<Faculty> response = testRestTemplate.getForEntity(mainUrl + "/{id}", Faculty.class, testFacultyId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void filterByColorOrNameTest_withColorParametr() throws Exception {
        Faculty newFaculty = new Faculty("Slytherin", "green");
        newFaculty = facultyRepository.save(newFaculty);
        List<Faculty> allFacultiesByColor = List.of(testFaculty, newFaculty);

        ResponseEntity<Faculty[]> response = testRestTemplate.getForEntity(mainUrl + "/filter?color=green",
                Faculty[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void allFacultiesInfoTest() throws Exception {
        Faculty newFaculty = new Faculty("Slytherin", "silver");
        newFaculty = facultyRepository.save(newFaculty);

        assertNotNull(newFaculty.getId());

        ResponseEntity<Collection<Faculty>> response = testRestTemplate.exchange(
                mainUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Faculty>>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Collection<Faculty> faculties = response.getBody();
        assertThat(faculties).isNotNull();
        assertThat(faculties).hasSize(2);

        List<String> allFacultiesNames = faculties.stream()
                .map(Faculty::getName)
                .toList();

        assertThat(allFacultiesNames).containsExactlyInAnyOrder("Gryffindor", "Slytherin");
    }
}
