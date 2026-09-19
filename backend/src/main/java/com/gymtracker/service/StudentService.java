package com.gymtracker.service;

import com.gymtracker.domain.Student;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class StudentService {

    @Transactional
    public Student createStudent(String userId, Student student) {
        student.userId = userId;
        student.persist();
        return student;
    }

    public List<Student> getStudents(String userId) {
        return Student.find("userId = ?1 ORDER BY name ASC", userId).list();
    }

    public Student getStudentById(String userId, Long studentId) {
        Student student = Student.findById(studentId);
        if (student != null && student.userId.equals(userId)) {
            return student;
        }
        return null;
    }

    @Transactional
    public Student updateStudent(String userId, Long studentId, Student updates) {
        Student student = getStudentById(userId, studentId);
        if (student != null) {
            student.name = updates.name;
            student.email = updates.email;
            student.phone = updates.phone;
            student.birthDate = updates.birthDate;
            student.notes = updates.notes;
            student.isActive = updates.isActive;
            student.persist();
        }
        return student;
    }

    @Transactional
    public boolean deleteStudent(String userId, Long studentId) {
        Student student = getStudentById(userId, studentId);
        if (student != null) {
            student.delete();
            return true;
        }
        return false;
    }
}
