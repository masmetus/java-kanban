package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import model.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {


    public HistoryHandler(InMemoryTaskManager managers) {
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
            List<Task> history = managers.getHistory();
            sendText(exchange, gson.toJson(history), 200);
        } catch (ManagerSaveException e) {
            sendNotFound(exchange);
        }
    }
}