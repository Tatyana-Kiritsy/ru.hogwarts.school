package ru.hogwarts.school.controller;


import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;
import java.util.Collection;


@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentService.findStudent(id);
    }

    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PutMapping("/{id}")
    public Student changeStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.editStudent(id, student);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.removeStudent(id);
    }

    @GetMapping
    public Collection<Student> allStudentsInfo() {
        return studentService.getAllStudents();
    }

    @GetMapping("/age")
    public Collection<Student> filterByAgeBetween(@RequestParam @Min(0) int ageMin,
                                                  @RequestParam @Min(0) int ageMax) {
        return studentService.findByAgeBetween(ageMin, ageMax);
    }

    @GetMapping("/faculty/{facultyId}/students")
    public Collection<Student> filterByFacultyId(@PathVariable Long facultyId) {
        return studentService.findAllStudentsByFacultyId(facultyId);
    }

    @GetMapping("/{studentId}/faculty")
    public String getFacultyNameByStudentId(@PathVariable Long studentId) {
        return studentService.getFacultyByStudentId(studentId);
    }
}
