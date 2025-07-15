package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(InMemoryTaskManager managers) {
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
                handleGetAllEpics(exchange);
            } else if (path.length == 3) {
                handleGetSingleEpic(exchange, path[2]);
            } else if (path.length == 4 && path[3].equals("subtasks")) {
                handleGetEpicSubtasks(exchange, path[2]);
            } else {
                sendNotFound(exchange);
            }

        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetSingleEpic(HttpExchange exchange, String EpicId) throws IOException {
        try {
            int id = Integer.parseInt(EpicId);
            Epic epic = managers.getEpicById(id);
            sendText(exchange, gson.toJson(epic), 200);

        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        } catch (ManagerSaveException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        try {
            List<Epic> epics = managers.getAllEpic();
            sendText(exchange, gson.toJson(epics), 200);
        } catch (ManagerSaveException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, String EpicId) throws IOException {
        try {
            int id = Integer.parseInt(EpicId);
            List<Subtask> subtasks = managers.getAllSubtaskOfEpic(id);
            sendText(exchange, gson.toJson(subtasks), 200);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    }

    private void postHandler(HttpExchange exchange) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            Epic epic = gson.fromJson(requestBody, Epic.class);

            managers.createEpic(epic);

            exchange.getResponseHeaders().set("Location", "/epics/" + epic.getId());
            sendCreated(exchange);

        } catch (ManagerSaveException e) {
            sendBadRequest(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }


    //Можно было бы передавать в метод id эпика, но тогда его надо откуда то брать. Наплодить кода для базового хедлера...
    //Проще так
    private void deleteHandler(HttpExchange exchange) throws IOException {
        try {
            String requestPath = exchange.getRequestURI().getPath();
            String[] path = requestPath.split("/");

            if (path.length < 3 || path[2].isEmpty()) {
                sendNotFound(exchange);
                return;
            }
            try {
                managers.removeEpicById(Integer.parseInt(path[2]));
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
