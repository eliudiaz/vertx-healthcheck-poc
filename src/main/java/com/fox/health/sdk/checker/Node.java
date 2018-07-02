package com.fox.health.sdk.checker;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Node {

    private String name;
    private String type;
    private String path;
    private boolean up;
    private long lastCheck;

    public synchronized void setAsUp(long time) {
        up = true;
        lastCheck = time;
    }

    public synchronized void setAsDown(long time) {
        up = false;
        lastCheck = time;
    }
}
