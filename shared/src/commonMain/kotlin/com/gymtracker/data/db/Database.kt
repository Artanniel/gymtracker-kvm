package com.gymtracker.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import com.gymtracker.db.GymDatabase
import kotlinx.datetime.Clock

object DatabaseProvider {
    private var db: GymDatabase? = null

    suspend fun init(factory: DatabaseDriverFactory): GymDatabase {
        if (db == null) {
            val driver: SqlDriver = factory.createDriver()
            db = GymDatabase(driver)
            seedIfNeeded()
        }
        return db!!
    }

    fun get(): GymDatabase = db ?: error("DatabaseProvider.init() não foi chamado")

    private suspend fun seedIfNeeded() {
        val d = db!!
        val existing = d.workoutSessionQueries.getAll().awaitAsList()
        if (existing.isNotEmpty()) return

        val now = Clock.System.now().toEpochMilliseconds()
        val day = 24L * 60 * 60 * 1000

        data class SeedSet(val weightKg: Double, val repsActual: Int, val setNumber: Int)
        data class SeedExercise(val id: String, val name: String, val sets: List<SeedSet>)
        data class SeedSession(val workoutId: String, val workoutName: String, val daysAgo: Long, val exercises: List<SeedExercise>)

        val sessions = listOf(
            SeedSession("treino_a", "Treino A — Quadríceps e Glúteo", 13, listOf(
                SeedExercise("a_agachamento_smith", "Agachamento Smith", listOf(
                    SeedSet(60.0, 12, 2), SeedSet(70.0, 10, 3), SeedSet(80.0, 8, 4)
                )),
                SeedExercise("a_leg45", "Leg 45", listOf(
                    SeedSet(90.0, 12, 2), SeedSet(100.0, 10, 3), SeedSet(110.0, 8, 4)
                )),
                SeedExercise("a_cadeira_extensora", "Cadeira Extensora", listOf(
                    SeedSet(30.0, 15, 2), SeedSet(35.0, 12, 3), SeedSet(40.0, 10, 4)
                ))
            )),
            SeedSession("treino_b", "Treino B — Peito e Ombro", 10, listOf(
                SeedExercise("b_supino_inclinado", "Supino Inclinado", listOf(
                    SeedSet(50.0, 12, 2), SeedSet(55.0, 10, 3), SeedSet(60.0, 8, 4)
                )),
                SeedExercise("b_supino_halter_inclinado", "Supino Halter Inclinado", listOf(
                    SeedSet(20.0, 12, 2), SeedSet(22.0, 10, 3), SeedSet(24.0, 8, 4)
                )),
                SeedExercise("b_elevacao_lateral", "Elevação Lateral", listOf(
                    SeedSet(8.0, 12, 2), SeedSet(10.0, 10, 3), SeedSet(10.0, 8, 4)
                ))
            )),
            SeedSession("treino_c", "Treino C — Dorsal", 6, listOf(
                SeedExercise("c_remada_curvada", "Remada Curvada", listOf(
                    SeedSet(50.0, 12, 2), SeedSet(55.0, 10, 3), SeedSet(60.0, 8, 4)
                )),
                SeedExercise("c_puxada_pronada", "Puxada Pronada", listOf(
                    SeedSet(40.0, 12, 2), SeedSet(45.0, 10, 3), SeedSet(50.0, 8, 4)
                )),
                SeedExercise("c_puxada_supinada", "Puxada Supinada", listOf(
                    SeedSet(35.0, 12, 2), SeedSet(40.0, 10, 3), SeedSet(45.0, 8, 4)
                ))
            )),
            SeedSession("treino_d", "Treino D — Bíceps e Tríceps", 2, listOf(
                SeedExercise("d_rosca_direta", "Rosca Direta", listOf(
                    SeedSet(12.0, 12, 3), SeedSet(14.0, 10, 4), SeedSet(16.0, 8, 5)
                )),
                SeedExercise("d_triceps_testa_barra", "Tríceps Testa Barra", listOf(
                    SeedSet(20.0, 12, 3), SeedSet(22.0, 10, 4), SeedSet(25.0, 8, 5)
                )),
                SeedExercise("d_martelo", "Martelo", listOf(
                    SeedSet(10.0, 12, 3), SeedSet(12.0, 10, 4), SeedSet(14.0, 8, 5)
                ))
            ))
        )

        sessions.forEach { session ->
            val sessionDate = now - session.daysAgo * day
            d.workoutSessionQueries.insert(session.workoutId, session.workoutName, sessionDate, "")
            val sessionId = d.workoutSessionQueries.lastInsertId().awaitAsOne()

            session.exercises.forEach { exercise ->
                exercise.sets.forEach { s ->
                    d.setLogQueries.insert(
                        sessionId, exercise.id, exercise.name,
                        s.setNumber.toLong(), "WORKING", "8 a 12",
                        s.weightKg, s.repsActual.toLong(), 1L, 180L
                    )
                }
            }
        }
    }
}
