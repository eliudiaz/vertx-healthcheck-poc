package com.fox.health.sdk.base;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;

import static java.util.Objects.nonNull;

public class RestBasedVerticle extends BaseVerticle<HttpServerRequest> {

    public static final String PING_PARAM = "ping";

    @Override
    public void handleRequest(HttpServerRequest request) {
        final String param = request.getParam(PING_PARAM);
        if (nonNull(param)) {
            request.response()
                    .putHeader("Content-Type", "text/plain")
                    .write(reply())
                    .end();
        }
    }

    public Router register(Router router) {
        router.get("/up").handler(h -> RestBasedVerticle.this.handleRequest(h.request()));
        return router;
    }
}
