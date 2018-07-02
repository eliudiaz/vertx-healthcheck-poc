package com.fox.health.sdk.base;

import io.vertx.core.AbstractVerticle;
import lombok.Data;

@Data
public abstract class BaseVerticle<T> extends AbstractVerticle {

    private String name;
    private int lastCheck;
    public static final String PING = "PING";
    public static final String PONG = "PONG";

    public abstract void handleRequest(T request);

    public boolean isPingRequest(String content) {
        return content.equalsIgnoreCase(PING);
    }

    public String reply() {
        return PONG;
    }

}
