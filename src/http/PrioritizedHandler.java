package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(InMemoryTaskManager managers) {
        super(managers);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    getHandler(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (IOException e) {
            sendInternalError(exchange);
        }

    }

    private void getHandler(HttpExchange exchange) throws IOException {
        try {
            List<Task> prioritized = managers.getPrioritizedTasks();
            sendText(exchange, gson.toJson(prioritized), 200);
        } catch (ManagerSaveException e) {
            sendNotFound(exchange);
        }
    }
}
