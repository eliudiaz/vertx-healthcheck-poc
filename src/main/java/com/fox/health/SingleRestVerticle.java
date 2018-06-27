package com.fox.health;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;

public class SingleRestVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        Router r = Router.router(vertx);
        r.get("/talk")
                .handler(h -> h.response().end("hello world!"));
        r.get("/up")
                .handler(h -> {
                    try {
                        Thread.sleep(1000);
                        h.response().end("Yes I am!");
                    } catch (InterruptedException e) {
                        h.fail(500);
                    }
                });
        vertx
                .createHttpServer()
                .requestHandler(r::accept)
                .listen(8081, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }
}