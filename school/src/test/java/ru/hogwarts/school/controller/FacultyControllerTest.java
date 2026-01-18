package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
class FacultyControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private FacultyService facultyService;

    @Test
    void getFacultyTest() throws Exception {
        Long id = 20L;
        String name = "Gryffindor";
        String color = "green";

        Faculty facultyTest = new Faculty();
        facultyTest.setName(name);
        facultyTest.setColor(color);
        facultyTest.setId(id);

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(facultyTest));

        mockMvc.perform(get("/faculty/" + facultyTest.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name").value(facultyTest.getName()))
                .andExpect(jsonPath("$.color").value(facultyTest.getColor()));
    }

    @Test
    void getFacultyByNotExistingIdTest() throws Exception {
        Long notExistingId = 999999L;

        when(facultyRepository.findById(notExistingId)).thenThrow(new FacultyNotFoundException(notExistingId));

        mockMvc.perform(get("/faculty/" + notExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addFacultyTest() throws Exception {
        Long id = 20L;
        String name = "Gryffindor";
        String color = "green";

        Faculty facultyTest = new Faculty();
        facultyTest.setName(name);
        facultyTest.setColor(color);
        facultyTest.setId(id);
        String content = objectMapper.writeValueAsString(facultyTest);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(facultyTest);

        mockMvc.perform(post("/faculty")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name").value(facultyTest.getName()))
                .andExpect(jsonPath("$.color").value(facultyTest.getColor()));
    }

    @Test
    void changeFacultyTest() throws Exception {
        Long id = 20L;
        String name = "Gryffindor";
        String color = "green";
        String updatedColor = "red";

        Faculty facultyTest = new Faculty(name, color);
        facultyTest.setId(id);
        Faculty updatedFacultyTest = new Faculty(name, updatedColor);
        updatedFacultyTest.setId(id);
        String content = objectMapper.writeValueAsString(facultyTest);

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(facultyTest));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(updatedFacultyTest);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty/{id}", id)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value(updatedColor))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void deleteFacultyTest() throws Exception {
        Long id = 20L;
        String name = "Gryffindor";
        String color = "green";

        Faculty facultyTest = new Faculty(name, color);
        facultyTest.setId(id);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(facultyTest));
        doNothing().when(facultyRepository).deleteById(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void filterByColorOrName_Color_Test() throws Exception {

        Faculty facultyTestOne = new Faculty("Gryffindor", "green");
        Faculty facultyTestTwo = new Faculty("Slytherin", "green");

        List<Faculty> expectedFaculties = Arrays.asList(facultyTestOne, facultyTestTwo);

        when(facultyRepository.findByColorOrNameContainingIgnoreCase("green", "green")).thenReturn(expectedFaculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filter")
                        .param("name", "green")
                        .param("color", "green")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color").value("green"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Slytherin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].color").value("green"));
    }

    @Test
    void getAllFacultiesInfoTest() throws Exception {
        Long id = 20L;
        String name = "Gryffindor";
        String color = "green";

        Faculty facultyTest = new Faculty(name, color);
        facultyTest.setId(id);
        List<Faculty> allFaculties = List.of(facultyTest);

        when(facultyRepository.findAll()).thenReturn(allFaculties);

        mockMvc.perform(get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("green"))
                .andExpect(jsonPath("$[0].id").value(20));

    }
}