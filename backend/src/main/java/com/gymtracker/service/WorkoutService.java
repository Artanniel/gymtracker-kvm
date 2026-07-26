package com.gymtracker.service;

import com.gymtracker.domain.SetLog;
import com.gymtracker.domain.WorkoutSession;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class WorkoutService {

    // ═══════════════════════════════════════════════════════════════════
    // SESSIONS
    // ═══════════════════════════════════════════════════════════════════

    @Transactional
    public WorkoutSession createSession(String userId, WorkoutSession session) {
        session.userId = userId;
        session.persist();
        return session;
    }

    public List<WorkoutSession> getSessions(String userId) {
        return WorkoutSession.find("userId = ?1 ORDER BY date DESC", userId).list();
    }

    public WorkoutSession getSessionById(String userId, String sessionId) {
        WorkoutSession session = WorkoutSession.findById(sessionId);
        if (session != null && session.userId.equals(userId)) {
            return session;
        }
        return null;
    }

    @Transactional
    public WorkoutSession updateSession(String userId, String sessionId, WorkoutSession updates) {
        WorkoutSession session = getSessionById(userId, sessionId);
        if (session != null) {
            session.workoutName = updates.workoutName;
            session.notes = updates.notes;
            session.persist();
        }
        return session;
    }

    @Transactional
    public boolean deleteSession(String userId, String sessionId) {
        WorkoutSession session = getSessionById(userId, sessionId);
        if (session != null) {
            SetLog.delete("sessionId = ?1", sessionId);
            session.delete();
            return true;
        }
        return false;
    }

    // ═══════════════════════════════════════════════════════════════════
    // SET LOGS
    // ═══════════════════════════════════════════════════════════════════

    @Transactional
    public SetLog createSetLog(String userId, SetLog setLog) {
        setLog.userId = userId;
        setLog.persist();
        return setLog;
    }

    public List<SetLog> getSetLogsBySession(String userId, String sessionId) {
        return SetLog.find("userId = ?1 AND sessionId = ?2 ORDER BY exerciseId, setNumber", userId, sessionId).list();
    }

    @Transactional
    public SetLog updateSetLog(String userId, String sessionId, String exerciseId, Long setNumber, SetLog updates) {
        SetLog setLog = SetLog.find("userId = ?1 AND sessionId = ?2 AND exerciseId = ?3 AND setNumber = ?4",
                userId, sessionId, exerciseId, setNumber).firstResult();
        if (setLog != null) {
            setLog.weightKg = updates.weightKg;
            setLog.repsActual = updates.repsActual;
            setLog.completed = updates.completed;
            setLog.restSeconds = updates.restSeconds;
            setLog.persist();
        }
        return setLog;
    }

    @Transactional
    public boolean deleteSetLogsBySession(String userId, String sessionId) {
        long deleted = SetLog.delete("userId = ?1 AND sessionId = ?2", userId, sessionId);
        return deleted > 0;
    }
}
