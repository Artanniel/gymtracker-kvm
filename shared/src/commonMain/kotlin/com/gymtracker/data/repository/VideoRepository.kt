package com.gymtracker.data.repository

import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.db.Exercise_videos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull

class VideoRepository {

    private val db get() = DatabaseProvider.get()
    private val videoQ get() = db.exerciseVideoQueries

    fun getAllVideosFlow(): Flow<List<Exercise_videos>> =
        videoQ.getAll().asFlow().mapToList(Dispatchers.Default)

    fun getByMuscleGroupFlow(muscleGroup: String): Flow<List<Exercise_videos>> =
        videoQ.getByMuscleGroup(muscleGroup).asFlow().mapToList(Dispatchers.Default)

    fun getByCategoryFlow(category: String): Flow<List<Exercise_videos>> =
        videoQ.getByCategory(category).asFlow().mapToList(Dispatchers.Default)

    suspend fun getByExercise(exerciseId: String): List<Exercise_videos> =
        videoQ.getByExercise(exerciseId).awaitAsList()

    suspend fun search(query: String): List<Exercise_videos> =
        videoQ.search(query, query).awaitAsList()

    suspend fun getCustomVideos(): List<Exercise_videos> =
        videoQ.getCustom().awaitAsList()

    suspend fun addVideo(
        exerciseId: String,
        title: String,
        url: String,
        thumbnailUrl: String? = null,
        duration: Long? = null,
        description: String? = null,
        muscleGroup: String,
        category: String,
        isCustom: Boolean = true
    ): Long {
        videoQ.insert(exerciseId, title, url, thumbnailUrl, duration, description, muscleGroup, category, if (isCustom) 1L else 0L)
        return videoQ.lastInsertId().awaitAsOne()
    }

    suspend fun updateVideo(
        id: Long,
        title: String,
        url: String,
        thumbnailUrl: String?,
        duration: Long?,
        description: String?,
        muscleGroup: String,
        category: String
    ) {
        videoQ.update(title, url, thumbnailUrl, duration, description, muscleGroup, category, id)
    }

    suspend fun deleteVideo(id: Long) {
        videoQ.delete(id)
    }

    suspend fun seedDefaultVideos() {
        val existing = videoQ.getAll().awaitAsList()
        if (existing.isNotEmpty()) return

        val defaultVideos = listOf(
            VideoSeed("agachamento", "Agachamento", "https://www.youtube.com/watch?v=UFs6E3Ti1jg", "Quadríceps", "Força"),
            VideoSeed("supino_reto", "Supino Reto", "https://www.youtube.com/watch?v=vcD71D8q93g", "Peito", "Força"),
            VideoSeed("remada_curvada", "Remada Curvada", "https://www.youtube.com/watch?v=kBdLmCjGQy8", "Costas", "Força"),
            VideoSeed("desenvolvimento", "Desenvolvimento", "https://www.youtube.com/watch?v=qEwKCR5JCog", "Ombros", "Força"),
            VideoSeed("rosca_direta", "Rosca Direta", "https://www.youtube.com/watch?v=ykJmrZ5v0Oo", "Bíceps", "Força"),
            VideoSeed("triceps_pulley", "Tríceps Pulley", "https://www.youtube.com/watch?v=2-LAMcpzODU", "Tríceps", "Força"),
            VideoSeed("leg_press", "Leg Press", "https://www.youtube.com/watch?v=IZxyjW7MPJQ", "Quadríceps", "Força"),
            VideoSeed("puxada_frente", "Puxada Frontal", "https://www.youtube.com/watch?v=CAwf7n6Luuc", "Costas", "Força"),
            VideoSeed("elevacao_lateral", "Elevação Lateral", "https://www.youtube.com/watch?v=3VcKaXpzqRo", "Ombros", "Força"),
            VideoSeed("abdominal_crunch", "Abdominal Crunch", "https://www.youtube.com/watch?v=XydFAZ91q30", "Abdômen", "Força"),
            VideoSeed("corrida_treadmill", "Corrida Esteira", "https://www.youtube.com/watch?v=a4V1W0bqYuc", "Cardio", "Cardio"),
            VideoSeed("burpees", "Burpees", "https://www.youtube.com/watch?v=TU8QYVW0gDU", "Full Body", "HIIT"),
            VideoSeed("prancha", "Prancha", "https://www.youtube.com/watch?v=ASdvN_XEl_c", "Core", "Flexibilidade"),
            VideoSeed("stiff", "Stiff", "https://www.youtube.com/watch?v=jEy_czb3RKA", "Posterior", "Força"),
            VideoSeed("cadeira_extensora", "Cadeira Extensora", "https://www.youtube.com/watch?v=YyvSfVjQeL0", "Quadríceps", "Força")
        )

        defaultVideos.forEach { v ->
            addVideo(v.exerciseId, v.title, v.url, null, null, null, v.muscleGroup, v.category, false)
        }
    }

    private data class VideoSeed(
        val exerciseId: String,
        val title: String,
        val url: String,
        val muscleGroup: String,
        val category: String
    )
}
