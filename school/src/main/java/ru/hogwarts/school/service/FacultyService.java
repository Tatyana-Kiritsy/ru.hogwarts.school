package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;


import java.util.*;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("CreateFaculty method was invoked");
        faculty.setId(null);
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(Long id) {
        return facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
    }

    public void removeFaculty(Long id) {
        logger.info("RemoveFaculty method was invoked");
        findFaculty(id);
        facultyRepository.deleteById(id);
    }

    public Faculty editFaculty(Long id, Faculty faculty) {
        logger.info("EditFaculty method was invoked");
        findFaculty(id);
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }

    public Collection<Faculty> findByColorOrName(String color, String name) {
        logger.info("FindByColorOrName method was invoked");
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
        logger.info("GetAllFaculties method was invoked");
        return facultyRepository.findAll();
    }
}
