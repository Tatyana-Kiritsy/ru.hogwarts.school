package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNameByStudentIdNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;


@Service
public class StudentService {

    private final StudentRepository studentRepository;

    private final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        logger.info("CreateStudent method was invoked");
        student.setId(null);
        return studentRepository.save(student);
    }

    public Student findStudent(Long id) {
        logger.info("FindStudent method was invoked");
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    public void removeStudent(Long id) {
        logger.info("RemoveStudent method was invoked");
        findStudent(id);
        studentRepository.deleteById(id);
    }

    public Student editStudent(Long id, Student student) {
        logger.info("EditStudent method was invoked");
        findStudent(id);
        student.setId(id);
        return studentRepository.save(student);
    }

    public Collection<Student> getAllStudents() {
        logger.info("GetAllStudents method was invoked");
        return studentRepository.findAll();
    }

    public Collection<Student> findByAgeBetween(int ageMin, int ageMax) {
        if (ageMin < 0 || ageMax < 0) {
            logger.error("Age must be positive!");
            throw new IllegalArgumentException("Возраст не может быть отрицательным!");
        }
        if (ageMin > ageMax) {
            logger.error("Wrong age order!");
            throw new IllegalArgumentException("Минимальный возраст не может " +
                    "быть больше максимального!");
        }
        Collection<Student> allStudentsByAgeBetween = getAllStudents();
        logger.info("FindByAge method was invoked");
        return allStudentsByAgeBetween.stream()
                .filter(s -> s.getAge() >= ageMin && s.getAge() <= ageMax)
                .toList();
    }

    public Collection<String> getStudentNamesStartedWith(String letter) {
        logger.info("GetStudentNamesStartedWith method was invoked");
        return studentRepository.findAll()
                .stream()
                .map(Student::getName)
                .filter(name -> name.toUpperCase()
                        .startsWith(letter.toUpperCase()))
                .sorted()
                .toList();
    }

    public Collection<Student> findAllStudentsByFacultyId(Long facultyId) {
        logger.info("FindAllStudentsByFacultyId method was invoked");
        return studentRepository.findByFacultyId(facultyId);
    }

    public String getFacultyByStudentId(Long studentId) {
        return studentRepository.findFacultyNameById(studentId)
                .orElseThrow(() -> new FacultyNameByStudentIdNotFoundException(studentId));
    }

    public long countAllStudents() {
        logger.info("CountAllStudents method was invoked");
        return studentRepository.countStudents();
    }

    public int countAverageAge() {
        logger.info("CountAverageAge method was invoked");
        return studentRepository.countAverageStudentsAge();
    }

    public Collection<Student> getFiveLastStudents() {
        logger.info("GetFiveLastStudents method was invoked");
        return studentRepository.findFiveLastStudents();
    }

    public int getAverageAge() {
        logger.info("GetAverageAge method was invoked");

        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new IllegalStateException("No students found");
        }
        return (int) Math.round(studentRepository.findAll()
                .stream()
                .mapToDouble(Student::getAge)
                .average()
                .orElseThrow());
    }

    public void printStudents() {
        List<Student> students = studentRepository.findAll();
        if (students.size() >= 6) {
            students.subList(0, 2).forEach(this::printStudentName);

            printStudents(students.subList(2, 4));
            printStudents(students.subList(4, 6));
        }
    }

    private void printStudents(List<Student> students) {
        new Thread(() -> {
            students.forEach(this::printStudentName);
        }).start();
    }

    private void printStudentName(Student student) {
        logger.info("Student's name: {} {}", student.getId(), student.getName());
    }

    public void printStudentsSynchronized() {
        List<Student> students = studentRepository.findAll();
        if (students.size() >= 6) {
            students.subList(0, 2).forEach(this::printStudentNameSynchronized);

            printStudentsSynchronized(students.subList(2, 4));
            printStudentsSynchronized(students.subList(4, 6));
        }
    }

    private void printStudentsSynchronized(List<Student> students) {
        new Thread(() -> {
            students.forEach(this::printStudentNameSynchronized);
        }).start();
    }

    private synchronized void printStudentNameSynchronized(Student student) {
        logger.info("Student's name synchronized: {} {}", student.getId(), student.getName());
    }
}
