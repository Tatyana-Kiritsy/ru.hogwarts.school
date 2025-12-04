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

    public Collection<Faculty> findByColorOrName(String color, String name) {
        if (color != null && name != null) {
            return facultyRepository.findByColorOrNameContainingIgnoreCase(color, name);
        } else if (color != null) {
            return facultyRepository.findByColorContainingIgnoreCase(color);
        } else if (name != null) {
            return facultyRepository.findByNameContainingIgnoreCase(name);
        } else {
            return null;
        }
    }

    public Collection<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }
}
