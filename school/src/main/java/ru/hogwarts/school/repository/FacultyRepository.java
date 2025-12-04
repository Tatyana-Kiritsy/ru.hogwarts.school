package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Collection<Faculty> findByColorContainingIgnoreCase(String colorPart);

    Collection<Faculty> findByNameContainingIgnoreCase(String name);

    Collection<Faculty> findByColorOrNameContainingIgnoreCase(String colorPart, String namePart);
}
