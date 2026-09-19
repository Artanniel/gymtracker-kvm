package com.gymtracker.service;

import com.gymtracker.domain.ExerciseVideo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class VideoService {

    public List<ExerciseVideo> getAllVideos() {
        return ExerciseVideo.findAll().list();
    }

    public List<ExerciseVideo> getByExercise(String exerciseId) {
        return ExerciseVideo.find("exerciseId = ?1 ORDER BY title", exerciseId).list();
    }

    public List<ExerciseVideo> getByMuscleGroup(String muscleGroup) {
        return ExerciseVideo.find("muscleGroup = ?1 ORDER BY title", muscleGroup).list();
    }

    public List<ExerciseVideo> search(String query) {
        return ExerciseVideo.find(
            "LOWER(title) LIKE LOWER(CONCAT('%',?1,'%')) OR LOWER(muscleGroup) LIKE LOWER(CONCAT('%',?1,'%'))",
            query
        ).list();
    }

    @Transactional
    public ExerciseVideo createVideo(String userId, ExerciseVideo video) {
        video.userId = userId;
        video.persist();
        return video;
    }

    @Transactional
    public ExerciseVideo updateVideo(Long id, ExerciseVideo updates) {
        ExerciseVideo video = ExerciseVideo.findById(id);
        if (video != null) {
            video.title = updates.title;
            video.url = updates.url;
            video.thumbnailUrl = updates.thumbnailUrl;
            video.duration = updates.duration;
            video.description = updates.description;
            video.muscleGroup = updates.muscleGroup;
            video.category = updates.category;
            video.persist();
        }
        return video;
    }

    @Transactional
    public boolean deleteVideo(Long id) {
        ExerciseVideo video = ExerciseVideo.findById(id);
        if (video != null) {
            video.delete();
            return true;
        }
        return false;
    }
}
