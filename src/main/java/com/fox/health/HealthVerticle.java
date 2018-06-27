package com.fox.health;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;

public class HealthVerticle extends AbstractVerticle {


    @Override
    public void start(Future<Void> fut) {
        HealthCheckHandler checker = HealthCheckHandler.create(vertx);

        //event bus based verticles
        String headlessTarget = HeadlessVerticle.class.getName();
        checker.register(headlessTarget, 1200, future -> {
            vertx.eventBus().send(headlessTarget, "ping", h -> {
                if (h.succeeded()) {
                    future.complete(Status.OK());
                } else {
                    future.complete(Status.KO());
                }
            });
        });

        //restful based verticles
        String restTarget = "http://localhost:8081/up";
        checker.register(SingleRestVerticle.class.getName(), 1000, future -> {
            WebClient client = WebClient.create(vertx);
            client.getAbs(restTarget)
                    .send(h -> {
                        if(!future.isComplete()) {
                            if (h.succeeded()) {
                                future.complete(Status.OK());
                            } else {
                                future.complete(Status.KO());
                            }
                        }
                    });
        });

        Router router = Router.router(vertx);
        router.get("/health").handler(checker::handle);
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }
}