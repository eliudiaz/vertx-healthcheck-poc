package com.fox.health;

import com.fox.health.sdk.base.RestBasedVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;

public class SingleRestVerticle extends RestBasedVerticle {

    @Override
    public void start(Future<Void> fut) {
        Router r = super.register(Router.router(vertx));
        r.get("/talk")
                .handler(h -> h.response().end("hello world!"));
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