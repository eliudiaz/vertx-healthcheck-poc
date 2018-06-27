package com.fox.health;

import io.vertx.core.Vertx;

public class Application {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HealthVerticle());
        vertx.deployVerticle(new HeadlessVerticle());
        vertx.deployVerticle(new SingleRestVerticle());
    }
}
