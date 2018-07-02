package com.fox.health.sdk.base;

import io.vertx.core.eventbus.Message;

public class MessageBasedVerticle<T extends String> extends BaseVerticle<Message<T>> {

    @Override
    public void handleRequest(Message<T> request) {
        if (isPingRequest(request.body())) {
            request.reply(reply());
        }
    }
}
