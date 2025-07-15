package http;

import com.sun.net.httpserver.HttpServer;
import manager.InMemoryTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private InMemoryTaskManager managers;

    public HttpTaskServer(InMemoryTaskManager managers) {
        this.managers = managers;
    }


    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(managers));
        server.createContext("/epics", new EpicHandler(managers));
        server.createContext("/subtasks", new SubtasksHandler(managers));
        server.createContext("/history", new HistoryHandler(managers));
        server.createContext("/prioritized", new PrioritizedHandler(managers));
        server.setExecutor(null);
        server.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }

    public static void main(String[] args) throws IOException {
        InMemoryTaskManager manager = new InMemoryTaskManager(); // или ваша реализация TaskManager
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
    }
}

