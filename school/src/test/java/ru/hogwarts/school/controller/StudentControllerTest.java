package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @SpyBean
    private StudentService studentService;


    @Test
    void getStudentTest() throws Exception {
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(11);
        student.setId(2L);
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(get("/student/" + student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()));
    }

    @Test
    void getStudentByNotExistingIdTest() throws Exception {
        Long notExistingId = 999999L;

        when(studentRepository.findById(notExistingId)).thenThrow(new StudentNotFoundException(notExistingId));

        mockMvc.perform(get("/student/" + notExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addStudentTest() throws Exception {
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(11);
        student.setId(2L);
        String content = objectMapper.writeValueAsString(student);

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/student")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()));
    }


    @Test
    void changeStudentTest() throws Exception {
        Long id = 1L;
        String name = "Ron Weasley";
        int age = 11;
        int newAge = 13;

        Student student = new Student(name, age, null);
        student.setId(id);
        Student updatedStudent = new Student(name, newAge, null);
        updatedStudent.setId(id);

        String content = objectMapper.writeValueAsString(student);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student/{id}", id)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(newAge))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void deleteStudentTest() throws Exception {
        Long studentId = 1L;
        String name = "Harry Potter";
        int age = 12;
        Student student = new Student(name, age, null);
        student.setId(studentId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        doNothing().when(studentRepository).deleteById(studentId);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/{id}", studentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getAllStudentsTest() throws Exception {
        Student student = new Student();
        student.setName("Ron Weasley");
        student.setAge(12);
        student.setId(1L);
        List<Student> students = List.of(student);

        when(studentRepository.findAll()).thenReturn(students);

        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[0].name").value("Ron Weasley"))
                .andExpect(jsonPath("$[0].age").value(12))
                .andExpect(jsonPath("$[0].id").value(1));

    }

    @Test
    void filterByAgeBetweenTest() throws Exception {
        List<Student> expectedStudents = Arrays.asList(
                new Student("Harry Potter", 12, null),
                new Student("Ron Weasley", 11, null));
        int ageMin = 10;
        int ageMax = 13;
        when(studentService.findByAgeBetween(ageMin, ageMax)).thenReturn(expectedStudents);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/age")
                        .param("ageMin", String.valueOf(ageMin))
                        .param("ageMax", String.valueOf(ageMax))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(12))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Ron Weasley"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].age").value(11));

    }

    @Test
    public void filterByAgeBetween_EmptyResult_Test() throws Exception {
        when(studentService.findByAgeBetween(40, 50))
                .thenReturn(List.of());
        mockMvc.perform(MockMvcRequestBuilders.get("/student/age")
                        .param("ageMin", "40")
                        .param("ageMax", "50"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }
}
