package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {


    public SubtasksHandler(InMemoryTaskManager managers) {
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

    private void getHandler(HttpExchange exchange) throws IOException {
        try {
            String requestPath = exchange.getRequestURI().getPath();
            String[] path = requestPath.split("/");

            if (path.length == 2) {
                handleGetAllSubtasks(exchange);
            } else if (path.length == 3) {
                handleGetSingleSubtasks(exchange, path[2]);
            } else {
                sendNotFound(exchange);
            }

        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetSingleSubtasks(HttpExchange exchange, String subId) throws IOException {
        try {
            int id = Integer.parseInt(subId);
            Subtask subtask = managers.getSubtaskById(id);
            sendText(exchange, gson.toJson(subtask), 200);

        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        } catch (ManagerSaveException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        try {
            List<Subtask> subtasks = managers.getAllSubtask();
            sendText(exchange, gson.toJson(subtasks), 200);

        } catch (ManagerSaveException e) {
            sendNotFound(exchange);
        }
    }


    private void postHandler(HttpExchange exchange) throws IOException {
        try {
            String requestPath = exchange.getRequestURI().getPath();
            String[] path = requestPath.split("/");

            if (path.length == 2 && path[1].equals("subtasks")) {
                handleCreateSubtask(exchange);
            } else if (path.length == 3 && path[1].equals("subtasks")) {
                handleUpdateSubtask(exchange, path[2]);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            Subtask subtask;

            subtask = gson.fromJson(requestBody, Subtask.class);
            if (subtask == null) {
                sendBadRequest(exchange);
                return;
            }

            if (subtask.getEpicId() <= 0) {
                sendBadRequest(exchange);
                return;
            }

            managers.createSubtask(subtask, subtask.getEpicId());
            exchange.getResponseHeaders().set("Location", "/subtasks/" + subtask.getId());
            sendCreated(exchange);

        } catch (ManagerSaveException e) {
            if (e.getMessage().contains("эпик не найден")) {
                sendNotFound(exchange);
            } else if (e.getMessage().contains("пересекается")) {
                sendHasOverlaps(exchange);
            } else {
                sendBadRequest(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange, String subId) throws IOException {
        try {
            int id = 0;

            try {
                id = Integer.parseInt(subId);
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
                return;
            }

            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            Subtask existing = managers.getSubtaskById(id);
            if (existing == null) {
                sendNotFound(exchange);
                return;
            }

            Subtask updateSubtask = gson.fromJson(requestBody, Subtask.class);

            if (updateSubtask.getId() != 0 && updateSubtask.getId() != id) {
                sendNotFound(exchange);
                return;
            }

            updateSubtask.setId(id);
            managers.updateSubtask(updateSubtask);
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

    private void deleteHandler(HttpExchange exchange) throws IOException {
        try {
            String requestPath = exchange.getRequestURI().getPath();
            String[] path = requestPath.split("/");

            if (path.length < 3 || path[2].isEmpty()) {
                sendNotFound(exchange);
                return;
            }
            try {
                managers.removeSubtaskById(Integer.parseInt(path[2]));
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
