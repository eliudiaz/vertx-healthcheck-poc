package com.fox.health;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;

import java.util.UUID;

public class HeadlessVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        vertx.eventBus().consumer(getClass().getName(), (Message<String> req) -> {
            try {
                switch (req.body()) {
                    case "ping":
                        Thread.sleep(1000);
                        req.reply("pong");
                        break;
                    default:
                        req.reply(UUID.randomUUID().toString());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                req.fail(500, "Interruption exception launched!");
            }
        });
        fut.complete();
    }
}