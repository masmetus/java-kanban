package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {


    public TasksHandler(InMemoryTaskManager managers) {
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
                case "POST":
                    postHandler(exchange);
                    break;
                case "DELETE":
                    deleteHandler(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (IOException e) {
            sendInternalError(exchange);
        }
    }

    //Хендлеры
    private void getHandler(HttpExchange exchange) throws IOException {
        try {
            String requestPath = exchange.getRequestURI().getPath();
            String[] path = requestPath.split("/");
            String jsonResponse;

            //GET tasks/{id}
            if (path.length > 2) {
                try {
                    Task task = managers.getTaskById(Integer.parseInt(path[2]));
                    jsonResponse = gson.toJson(task);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange);
                    return;
                } catch (ManagerSaveException e) {
                    sendNotFound(exchange);
                    return;
                }
            } else {
                jsonResponse = gson.toJson(managers.getAllTask());
            }
            sendText(exchange, jsonResponse, 200);

        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void postHandler(HttpExchange exchange) throws IOException {
        try {
            String requestPath = exchange.getRequestURI().getPath();
            String[] path = requestPath.split("/");
            if (path.length == 2) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                Task task = gson.fromJson(requestBody, Task.class);

                managers.createTask(task);

                exchange.getResponseHeaders().set("Location", "/tasks/" + task.getId());
                sendCreated(exchange);
            } else {
                try {
                    if (path.length < 3) {
                        sendNotFound(exchange);
                        return;
                    }
                    int taskId = 0;

                    try {
                        taskId = Integer.parseInt(path[2]);
                    } catch (NumberFormatException e) {
                        sendBadRequest(exchange);
                    }

                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                    Task updateTask = gson.fromJson(requestBody, Task.class);

                    if (updateTask.getId() != 0 && updateTask.getId() != taskId) {
                        sendBadRequest(exchange);
                        return;
                    }

                    updateTask.setId(taskId);
                    managers.updateTask(updateTask);
                    sendCreated(exchange);

                } catch (NumberFormatException e) {
                    sendBadRequest(exchange);
                } catch (ManagerSaveException e) {
                    if (e.getMessage().contains("пересекается")) {
                        sendHasOverlaps(exchange);
                    } else
                        sendNotFound(exchange);
                }

            }

        } catch (ManagerSaveException e) {
            sendHasOverlaps(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void deleteHandler(HttpExchange exchange) throws IOException {
        try {
            String requestPath = exchange.getRequestURI().getPath();
            String[] path = requestPath.split("/");
            try {
                if (path.length < 3 || path[2].isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }
                managers.removeTaskById(Integer.parseInt(path[2]));
                sendSuccess(exchange);

            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            } catch (ManagerSaveException e) {
                sendNotFound(exchange);
            }

        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
