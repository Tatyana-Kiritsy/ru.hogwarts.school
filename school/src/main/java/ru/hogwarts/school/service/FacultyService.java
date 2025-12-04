package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;


import java.util.*;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(null);
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(Long id) {
        return facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
    }

    public void removeFaculty(Long id) {
        findFaculty(id);
        facultyRepository.deleteById(id);
    }

    public Faculty editFaculty(Long id, Faculty faculty) {
        findFaculty(id);
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }

    public Collection<Faculty> findByColor(String color) {
        return facultyRepository.findByColorContainingIgnoreCase(color);
    }

    public Collection<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }
}
