package com.gymtracker.controller;

import com.gymtracker.domain.Student;
import com.gymtracker.service.StudentService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentController {

    @Inject
    StudentService studentService;

    @Inject
    JsonWebToken jwt;

    private String getUserId() {
        if (jwt == null || jwt.getSubject() == null) {
            throw new WebApplicationException("Authentication required", Response.Status.UNAUTHORIZED);
        }
        return jwt.getSubject();
    }

    @GET
    public RestResponse<List<Student>> listAll() {
        return RestResponse.ok(studentService.getStudents(getUserId()));
    }

    @GET
    @Path("/{id}")
    public RestResponse<Student> getById(@PathParam("id") Long id) {
        Student student = studentService.getStudentById(getUserId(), id);
        if (student == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(student);
    }

    @POST
    public RestResponse<Student> create(@Valid Student student) {
        Student created = studentService.createStudent(getUserId(), student);
        return RestResponse.status(Response.Status.CREATED, created);
    }

    @PUT
    @Path("/{id}")
    public RestResponse<Student> update(@PathParam("id") Long id, @Valid Student student) {
        Student updated = studentService.updateStudent(getUserId(), id, student);
        if (updated == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(updated);
    }

    @DELETE
    @Path("/{id}")
    public RestResponse<Void> delete(@PathParam("id") Long id) {
        boolean deleted = studentService.deleteStudent(getUserId(), id);
        if (!deleted) {
            return RestResponse.notFound();
        }
        return RestResponse.noContent();
    }
}
