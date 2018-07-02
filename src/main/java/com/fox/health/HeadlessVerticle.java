package com.fox.health;

import com.fox.health.sdk.base.MessageBasedVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;

import java.util.UUID;

public class HeadlessVerticle extends MessageBasedVerticle<String> {

    @Override
    public void start(Future<Void> fut) {
        vertx.eventBus().consumer(getClass().getName(), (Message<String> req) -> {
            try {
                HeadlessVerticle.this.handleRequest(req);
                Thread.sleep(1000);
                req.reply(UUID.randomUUID().toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
                req.fail(500, "Interruption exception launched!");
            }
        });
        fut.complete();
    }
}