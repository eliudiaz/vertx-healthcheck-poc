package com.fox.health.sdk.checker;

import com.fox.health.sdk.base.BaseVerticle;
import com.fox.health.sdk.base.RestBasedVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HealthCheckerService extends AbstractVerticle {

    private List<Node> nodes = new ArrayList<>();

    private void registerNodes(HealthCheckHandler checker) {
        nodes.stream().forEach(node -> {
            switch (node.getType()) {
                case "message":
                    checker.register(node.getName(), buildMessageBasedHandler(node));
                    break;
                case "rest":
                    checker.register(node.getName(), buildRestBasedHandler(node));
                    break;
            }
        });
    }

    public Handler<Future<Status>> buildMessageBasedHandler(final Node node) {
        return future ->
                vertx.eventBus().send(node.getPath(), BaseVerticle.PING, r -> {
                    if (r.succeeded()) {
                        future.complete(Status.OK());
                    } else {
                        future.complete(Status.KO());
                    }
                });
    }

    public Handler<Future<Status>> buildRestBasedHandler(final Node node) {
        return future ->
                WebClient.create(vertx)
                        .getAbs(node.getPath().concat("?").concat(RestBasedVerticle.PING_PARAM).concat("=1"))
                        .send(h -> {
                            if (!future.isComplete()) {
                                if (h.succeeded()) {
                                    future.complete(Status.OK());
                                } else {
                                    future.complete(Status.KO());
                                }
                            }
                        });
    }

    private void setNodes(List<Map<String,String>> nodes, Handler<AsyncResult<Void>> handler) {
        vertx.runOnContext(v -> {
            nodes.forEach(n
                    -> HealthCheckerService.this.nodes.add(Node
                    .builder()
                    .name(n.get("name"))
                    .type(n.get("type"))
                    .path(n.get("path"))
                    .build()));
            handler.handle(Future.succeededFuture());
        });
    }

    @Override
    public void start(Future<Void> fut) {
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", "health-config.json"));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);
        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        HealthCheckHandler checker = HealthCheckHandler.create(vertx);

        retriever.getConfig(reg -> {
            if (reg.succeeded()) {
                List cfgNodes = reg.result().getJsonArray("nodes").getList();
                HealthCheckerService.this.setNodes(cfgNodes, h -> {
                    registerNodes(checker);
                    Router router = Router.router(vertx);
                    router.get("/health").handler(checker::handle);
                    vertx
                            .createHttpServer()
                            .requestHandler(router::accept)
                            .listen(8081, result -> {
                                if (result.succeeded()) {
                                    System.out.println("listening!");
                                    fut.complete();
                                } else {
                                    fut.fail(result.cause());
                                }
                            });
                });


            }
        });

    }

}
