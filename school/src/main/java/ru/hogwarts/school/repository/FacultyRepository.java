package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Collection<Faculty> findByColorContainingIgnoreCase(String color);

    Collection<Faculty> findByNameContainingIgnoreCase(String name);

    Collection<Faculty> findByColorOrNameContainingIgnoreCase(String colorPart, String namePart);



}
