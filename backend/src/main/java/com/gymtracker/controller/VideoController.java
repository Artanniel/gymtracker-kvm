package com.gymtracker.controller;

import com.gymtracker.domain.ExerciseVideo;
import com.gymtracker.service.VideoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api/videos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VideoController {

    @Inject
    VideoService videoService;

    @Inject
    JsonWebToken jwt;

    private String getUserId() {
        if (jwt == null || jwt.getSubject() == null) {
            throw new WebApplicationException("Authentication required", Response.Status.UNAUTHORIZED);
        }
        return jwt.getSubject();
    }

    @GET
    public RestResponse<List<ExerciseVideo>> listAll() {
        return RestResponse.ok(videoService.getAllVideos());
    }

    @GET
    @Path("/exercise/{exerciseId}")
    public RestResponse<List<ExerciseVideo>> getByExercise(@PathParam("exerciseId") String exerciseId) {
        return RestResponse.ok(videoService.getByExercise(exerciseId));
    }

    @GET
    @Path("/muscle/{muscleGroup}")
    public RestResponse<List<ExerciseVideo>> getByMuscle(@PathParam("muscleGroup") String muscleGroup) {
        return RestResponse.ok(videoService.getByMuscleGroup(muscleGroup));
    }

    @GET
    @Path("/search")
    public RestResponse<List<ExerciseVideo>> search(@QueryParam("q") String query) {
        return RestResponse.ok(videoService.search(query));
    }

    @POST
    public RestResponse<ExerciseVideo> create(@Valid ExerciseVideo video) {
        ExerciseVideo created = videoService.createVideo(getUserId(), video);
        return RestResponse.status(Response.Status.CREATED, created);
    }

    @PUT
    @Path("/{id}")
    public RestResponse<ExerciseVideo> update(@PathParam("id") Long id, @Valid ExerciseVideo video) {
        ExerciseVideo updated = videoService.updateVideo(id, video);
        if (updated == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(updated);
    }

    @DELETE
    @Path("/{id}")
    public RestResponse<Void> delete(@PathParam("id") Long id) {
        boolean deleted = videoService.deleteVideo(id);
        if (!deleted) {
            return RestResponse.notFound();
        }
        return RestResponse.noContent();
    }
}
