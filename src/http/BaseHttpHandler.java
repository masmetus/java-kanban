package http;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import adapter.StatusAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.InMemoryTaskManager;
import model.Status;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {

    protected final InMemoryTaskManager managers;

    protected final Gson gson;

    public BaseHttpHandler(InMemoryTaskManager managers) {
        this.managers = managers;
        this.gson = createGson();
    }

    protected Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }


    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "Not Found", 404);
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        sendText(exchange, "Bad Request", 400);
    }

    protected void sendCreated(HttpExchange exchange) throws IOException {
        sendText(exchange, "Created", 201);
    }

    protected void sendSuccess(HttpExchange exchange) throws IOException {
        sendText(exchange, "Sucess", 200);
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        sendText(exchange, "Not Acceptable", 406);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        sendText(exchange, "Internal Error", 500);
    }


}

