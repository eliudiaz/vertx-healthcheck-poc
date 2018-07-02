package com.fox.health;

import com.fox.health.sdk.checker.HealthCheckerService;
import io.vertx.core.Vertx;

public class Application {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HealthCheckerService());
        vertx.deployVerticle(new HeadlessVerticle());
        vertx.deployVerticle(new SingleRestVerticle());
    }
}
