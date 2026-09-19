package com.gymtracker.data.repository

import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.db.Student_feedback
import com.gymtracker.db.Students
import com.gymtracker.db.Student_workouts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import kotlinx.datetime.Clock

class StudentRepository {

    private val db get() = DatabaseProvider.get()
    private val studentQ get() = db.studentQueries
    private val swQ get() = db.studentWorkoutQueries
    private val fbQ get() = db.studentFeedbackQueries

    // ── Students ──

    fun getAllStudentsFlow(): Flow<List<Students>> =
        studentQ.getAll().asFlow().mapToList(Dispatchers.Default)

    fun getActiveStudentsFlow(): Flow<List<Students>> =
        studentQ.getActive().asFlow().mapToList(Dispatchers.Default)

    suspend fun getStudentById(id: Long): Students? =
        studentQ.getById(id).awaitAsOneOrNull()

    suspend fun searchStudents(query: String): List<Students> =
        studentQ.searchByName(query).awaitAsList()

    suspend fun createStudent(
        name: String,
        email: String? = null,
        phone: String? = null,
        birthDate: Long? = null,
        notes: String = ""
    ): Long {
        val now = Clock.System.now().toEpochMilliseconds()
        studentQ.insert(name, email, phone, birthDate, notes, 1L, now, now)
        return studentQ.lastInsertId().awaitAsOne()
    }

    suspend fun updateStudent(
        id: Long,
        name: String,
        email: String?,
        phone: String?,
        birthDate: Long?,
        notes: String,
        isActive: Boolean
    ) {
        val now = Clock.System.now().toEpochMilliseconds()
        studentQ.update(name, email, phone, birthDate, notes, if (isActive) 1L else 0L, now, id)
    }

    suspend fun deactivateStudent(id: Long) {
        val now = Clock.System.now().toEpochMilliseconds()
        studentQ.update(
            name = "", email = null, phone = null, birthDate = null,
            notes = "", isActive = 0L, updatedAt = now, id = id
        )
    }

    suspend fun deleteStudent(id: Long) {
        studentQ.delete(id)
    }

    // ── Student-Workout Assignment ──

    fun getStudentWorkoutsFlow(studentId: Long): Flow<List<Student_workouts>> =
        swQ.getByStudent(studentId).asFlow().mapToList(Dispatchers.Default)

    suspend fun assignWorkout(studentId: Long, workoutId: String) {
        swQ.insert(studentId, workoutId)
    }

    suspend fun unassignWorkout(studentId: Long, workoutId: String) {
        swQ.delete(studentId, workoutId)
    }

    suspend fun getStudentWorkouts(studentId: Long): List<Student_workouts> =
        swQ.getByStudent(studentId).awaitAsList()

    // ── Student Feedback ──

    fun getStudentFeedbackFlow(studentId: Long): Flow<List<Student_feedback>> =
        fbQ.getByStudent(studentId).asFlow().mapToList(Dispatchers.Default)

    suspend fun addFeedback(studentId: Long, sessionId: Long, rating: Int, message: String) {
        fbQ.insert(studentId, sessionId, rating.toLong(), message)
    }

    suspend fun getFeedbackForSession(sessionId: Long): Student_feedback? =
        fbQ.getBySession(sessionId).awaitAsOneOrNull()

    suspend fun getStudentCount(): Long {
        return studentQ.getAll().awaitAsList().size.toLong()
    }

    suspend fun getActiveStudentCount(): Long {
        return studentQ.getActive().awaitAsList().size.toLong()
    }
}
