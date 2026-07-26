package com.gymtracker.dto;

public class SyncResponse {
    public boolean success;
    public String message;
    public String serverEntityId;
    public Long serverTimestamp;

    public static SyncResponse ok(String serverEntityId, Long serverTimestamp) {
        SyncResponse r = new SyncResponse();
        r.success = true;
        r.message = "OK";
        r.serverEntityId = serverEntityId;
        r.serverTimestamp = serverTimestamp;
        return r;
    }

    public static SyncResponse error(String message) {
        SyncResponse r = new SyncResponse();
        r.success = false;
        r.message = message;
        return r;
    }
}
