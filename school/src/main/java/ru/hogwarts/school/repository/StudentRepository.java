package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAgeBetween(int ageMin, int ageMax);

    Collection<Student> findByFacultyId(Long facultyId);

    @Query("SELECT s.faculty.name FROM Student s WHERE s.id = :studentId")
    Optional<String> findFacultyNameById(Long studentId);
}
