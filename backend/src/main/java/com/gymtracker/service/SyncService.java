package com.gymtracker.service;

import com.gymtracker.domain.SetLog;
import com.gymtracker.domain.WorkoutSession;
import com.gymtracker.dto.SyncRequest;
import com.gymtracker.dto.SyncResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SyncService {

    @Inject
    WorkoutService workoutService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public List<SyncResponse> processSyncBatch(String userId, List<SyncRequest> requests) {
        List<SyncResponse> responses = new ArrayList<>();

        for (SyncRequest request : requests) {
            try {
                SyncResponse response = processSingleSync(userId, request);
                responses.add(response);
            } catch (Exception e) {
                responses.add(SyncResponse.error("Error processing " + request.entityType + ": " + e.getMessage()));
            }
        }

        return responses;
    }

    private SyncResponse processSingleSync(String userId, SyncRequest request) {
        switch (request.entityType) {
            case "workout_session":
                return syncWorkoutSession(userId, request);
            case "set_log":
                return syncSetLog(userId, request);
            default:
                return SyncResponse.error("Unknown entity type: " + request.entityType);
        }
    }

    private SyncResponse syncWorkoutSession(String userId, SyncRequest request) {
        try {
            WorkoutSession session = objectMapper.readValue(request.payload, WorkoutSession.class);

            switch (request.action) {
                case "CREATE":
                    WorkoutSession created = workoutService.createSession(userId, session);
                    return SyncResponse.ok(created.id, System.currentTimeMillis());

                case "UPDATE":
                    WorkoutSession updated = workoutService.updateSession(userId, request.entityId, session);
                    if (updated != null) {
                        return SyncResponse.ok(updated.id, System.currentTimeMillis());
                    }
                    return SyncResponse.error("Session not found");

                case "DELETE":
                    boolean deleted = workoutService.deleteSession(userId, request.entityId);
                    if (deleted) {
                        return SyncResponse.ok(request.entityId, System.currentTimeMillis());
                    }
                    return SyncResponse.error("Session not found");

                default:
                    return SyncResponse.error("Unknown action: " + request.action);
            }
        } catch (Exception e) {
            return SyncResponse.error("Failed to sync workout_session: " + e.getMessage());
        }
    }

    private SyncResponse syncSetLog(String userId, SyncRequest request) {
        try {
            SetLog setLog = objectMapper.readValue(request.payload, SetLog.class);

            switch (request.action) {
                case "CREATE":
                    SetLog created = workoutService.createSetLog(userId, setLog);
                    return SyncResponse.ok(created.id, System.currentTimeMillis());

                case "UPDATE":
                    SetLog updated = workoutService.updateSetLog(
                            userId, setLog.sessionId, setLog.exerciseId, setLog.setNumber, setLog);
                    if (updated != null) {
                        return SyncResponse.ok(updated.id, System.currentTimeMillis());
                    }
                    return SyncResponse.error("Set log not found");

                case "DELETE":
                    boolean deleted = workoutService.deleteSetLogsBySession(userId, setLog.sessionId);
                    if (deleted) {
                        return SyncResponse.ok(setLog.sessionId, System.currentTimeMillis());
                    }
                    return SyncResponse.error("Set logs not found");

                default:
                    return SyncResponse.error("Unknown action: " + request.action);
            }
        } catch (Exception e) {
            return SyncResponse.error("Failed to sync set_log: " + e.getMessage());
        }
    }

    public List<Object> pullData(String userId, String entityType, Long since) {
        List<Object> data = new ArrayList<>();

        switch (entityType) {
            case "workout_sessions":
                data.addAll(workoutService.getSessions(userId));
                break;
            case "set_logs":
                // Would need additional service method for this
                break;
            default:
                break;
        }

        return data;
    }
}
