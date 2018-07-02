package com.fox.health.sdk.base;

import io.vertx.core.http.HttpServerRequest;

import static java.util.Objects.nonNull;

public class RestBasedVerticle extends BaseVerticle<HttpServerRequest> {

    public static final String PING_PARAM = "ping";

    @Override
    void handleRequest(HttpServerRequest request) {
        final String param = request.getParam(PING_PARAM);
        if (nonNull(param)) {
            request.response()
                    .putHeader("Content-Type", "text/plain")
                    .write(reply())
                    .end();
        }
    }
}
