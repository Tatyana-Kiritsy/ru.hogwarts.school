package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNameByStudentIdNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import java.util.*;


@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        student.setId(null);
        return studentRepository.save(student);
    }

    public Student findStudent(Long id) {

        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    public void removeStudent(Long id) {
        findStudent(id);
        studentRepository.deleteById(id);
    }

    public Student editStudent(Long id, Student student) {
        findStudent(id);
        student.setId(id);
        return studentRepository.save(student);
    }

    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Collection<Student> findByAgeBetween(int ageMin, int ageMax) {
        if (ageMin < 0 || ageMax < 0) {
            throw new IllegalArgumentException("Возраст не может быть отрицательным!");
        }
        if (ageMin > ageMax) {
            throw new IllegalArgumentException("Минимальный возраст не может " +
                    "быть больше максимального!");
        }
        Collection<Student> allStudentsByAgeBetween = getAllStudents();
        return allStudentsByAgeBetween.stream()
                .filter(s -> s.getAge() >= ageMin && s.getAge() <= ageMax)
                .toList();
    }

    public Collection<Student> findAllStudentsByFacultyId(Long facultyId) {
        return studentRepository.findByFacultyId(facultyId);
    }

    public String getFacultyByStudentId(Long studentId) {
        return studentRepository.findFacultyNameById(studentId)
                .orElseThrow(() -> new FacultyNameByStudentIdNotFoundException(studentId));
    }
}
