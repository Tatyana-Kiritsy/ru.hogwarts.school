package ru.hogwarts.school.controller;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;

import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;


@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/{id}")
    public Faculty getFaculty(@PathVariable Long id) {
        return facultyService.findFaculty(id);
    }

    @PostMapping
    public Faculty addFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PutMapping("/{id}")
    public Faculty changeFaculty(@PathVariable Long id, @RequestBody Faculty faculty) {
        return facultyService.editFaculty(id, faculty);
    }

    @DeleteMapping("/{id}")
    public void deleteFaculty(@PathVariable Long id) {
        facultyService.removeFaculty(id);
    }

    @GetMapping("/filter")
    public Collection<Faculty> filterByColor(@RequestParam @NotBlank String color) {
        return facultyService.findByColor(color);
    }

    @GetMapping
    public Collection<Faculty> allFacultiesInfo() {
        return facultyService.getAllFaculties();
    }
}

