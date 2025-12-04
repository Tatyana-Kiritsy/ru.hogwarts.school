package ru.hogwarts.school.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FacultyNameByStudentIdNotFoundException extends RuntimeException {
    public FacultyNameByStudentIdNotFoundException(Long studentId) {
        super("Имя факультета по идентификатору студента " + studentId + "  не найдено!");
    }
}
