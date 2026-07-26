package com.gymtracker.dto;

import java.util.List;

public class SyncRequest {
    public String entityType;
    public String entityId;
    public String action;
    public String payload;
    public Long clientTimestamp;
}
