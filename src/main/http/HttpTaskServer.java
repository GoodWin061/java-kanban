package main.http;
import main.task.*;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import main.manager.TaskManager;
import main.history.HistoryManager;
import main.manager.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private HttpServer httpServer;
    private final TaskManager taskManager;
    private final HistoryManager historyManager;

    public HttpTaskServer() {
        this(Managers.getDefault(), Managers.getDefaultHistory());
    }

    public HttpTaskServer(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
    }

    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer();
        try {
            taskServer.start();
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        System.out.println("Сервер запущен на порту " + PORT);

        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(historyManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));

        httpServer.start();
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
            System.out.println("Сервер остановлен");
        }
    }

    public class TasksHandler extends BaseHttpHandler implements HttpHandler {

        private final TaskManager taskManager;
        private final Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();


        public TasksHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            try {
                if ("GET".equalsIgnoreCase(method)) {
                    handleGet(exchange, path);
                } else if ("POST".equalsIgnoreCase(method) && "/tasks".equals(path)) {
                    handlePost(exchange);
                } else if ("DELETE".equalsIgnoreCase(method) && path.startsWith("/tasks/")) {
                    handleDelete(exchange, path);
                } else {
                    sendText(exchange, "Ошибка: Метод или путь не поддерживается", 405);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendText(exchange, "Ошибка: Внутренняя ошибка сервера", 500);
            }
        }

        private void handleGet(HttpExchange exchange, String path) throws IOException {
            String[] segments = path.split("/");
            if (segments.length == 2 && "tasks".equals(segments[1])) {
                var tasks = taskManager.getAllTasks();
                String jsonResponse = gson.toJson(tasks);
                sendText(exchange, jsonResponse, 200);
            } else if (segments.length == 3 && "tasks".equals(segments[1])) {
                try {
                    int id = Integer.parseInt(segments[2]);
                    Task task = taskManager.getIdTask(id);
                    if (task != null) {
                        historyManager.add(task);
                        String jsonResponse = gson.toJson(task);
                        sendText(exchange, jsonResponse, 200);
                    } else {
                        sendNotFound(exchange);
                    }
                } catch (NumberFormatException e) {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(),"UTF-8");
            Task task;
            try {
                task = gson.fromJson(body, Task.class);
            } catch (Exception e) {
                sendText(exchange, "Ошибка: Некорректный формат JSON", 400);
                return;
            }

            try {
                if (task.getId() == null) {
                    taskManager.addTask(task);
                    String jsonResponse = gson.toJson(task);
                    sendText(exchange, jsonResponse, 201);
                } else {
                    int id = task.getId();
                    taskManager.updateTask(id, task);
                    sendText(exchange, "Сообщение: Задача с id " + id + " обновлена", 201);
                }
            } catch (IllegalArgumentException e) {
                sendHasOverlaps(exchange);
            }
        }

        private void handleDelete(HttpExchange exchange, String path) throws IOException {
            String idStr = path.substring("/tasks/".length());
            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                sendText(exchange, "Ошибка: Некорректный id задачи", 400);
                return;
            }

            try {
                taskManager.deleteTask(id);
                sendText(exchange, "Сообщение: Задача с id " + id + " удалена", 200);
            } catch (NoSuchElementException e) {
                sendNotFound(exchange);
            }
        }
    }

    static class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
        private final TaskManager taskManager;
        private final Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        public SubtasksHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            try {
                if ("GET".equalsIgnoreCase(method)) {
                    handleGet(exchange, path);
                } else if ("POST".equalsIgnoreCase(method) && "/subtasks".equals(path)) {
                    handlePost(exchange);
                } else if ("DELETE".equalsIgnoreCase(method) && path.startsWith("/subtasks/")) {
                    handleDelete(exchange, path);
                } else {
                    sendText(exchange, "Ошибка: Метод или путь не поддерживается", 405);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendText(exchange, "Ошибка: Внутренняя ошибка сервера", 500);
            }
        }

        private void handleGet(HttpExchange exchange, String path) throws IOException {
            String[] segments = path.split("/");
            if (segments.length == 2 && "subtasks".equals(segments[1])) {
                var subtasks = taskManager.getAllSubTask();
                String jsonResponse = gson.toJson(subtasks);
                sendText(exchange, jsonResponse, 200);
            } else if (segments.length == 3 && "subtasks".equals(segments[1])) {
                try {
                    int id = Integer.parseInt(segments[2]);
                    SubTask subtask = taskManager.getIdSubTask(id);
                    if (subtask != null) {
                        String jsonResponse = gson.toJson(subtask);
                        sendText(exchange, jsonResponse, 200);
                    } else {
                        sendNotFound(exchange);
                    }
                } catch (NumberFormatException e) {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
            SubTask subtask;
            try {
                subtask = gson.fromJson(body, SubTask.class);
            } catch (Exception e) {
                sendText(exchange, "Ошибка: Некорректный формат JSON", 400);
                return;
            }

            try {
                if (subtask.getId() == null) {
                    taskManager.addSubTask(subtask);
                    String jsonResponse = gson.toJson(subtask);
                    sendText(exchange, jsonResponse, 201);
                } else {
                    int id = subtask.getId();
                    taskManager.updateSubTask(id, subtask);
                    sendText(exchange, "Сообщение: Подзадача с id " + id + " обновлена", 201);
                }
            } catch (IllegalArgumentException e) {
                sendHasOverlaps(exchange);
            }
        }

        private void handleDelete(HttpExchange exchange, String path) throws IOException {
            String idStr = path.substring("/subtasks/".length());
            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                sendText(exchange, "Ошибка: Некорректный id подзадачи", 400);
                return;
            }

            try {
                taskManager.deleteSubTask(id);
                sendText(exchange, "Сообщение: Подзадача с id " + id + " удалена", 200);
            } catch (NoSuchElementException e) {
                sendNotFound(exchange);
            }
        }
    }

    static class EpicsHandler extends BaseHttpHandler implements HttpHandler {
        private final TaskManager taskManager;
        private final Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();


        public EpicsHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            try {
                if ("GET".equalsIgnoreCase(method)) {
                    handleGet(exchange, path);
                } else if ("POST".equalsIgnoreCase(method) && "/epics".equals(path)) {
                    handlePost(exchange);
                } else if ("DELETE".equalsIgnoreCase(method) && path.startsWith("/epics/")) {
                    handleDelete(exchange, path);
                } else {
                    sendText(exchange, "Ошибка: Метод или путь не поддерживается", 405);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendText(exchange, "Ошибка: Внутренняя ошибка сервера", 500);
            }
        }

        private void handleGet(HttpExchange exchange, String path) throws IOException {
            String[] segments = path.split("/");
            if (segments.length == 2 && "epics".equals(segments[1])) {
                var epics = taskManager.getAllEpic();
                String jsonResponse = gson.toJson(epics);
                sendText(exchange, jsonResponse, 200);
            } else if (segments.length == 3 && "epics".equals(segments[1])) {
                try {
                    int id = Integer.parseInt(segments[2]);
                    Epic epic = taskManager.getIdEpic(id);
                    if (epic != null) {
                        String jsonResponse = gson.toJson(epic);
                        sendText(exchange, jsonResponse, 200);
                    } else {
                        sendNotFound(exchange);
                    }
                } catch (NumberFormatException e) {
                    sendNotFound(exchange);
                }
            } else if (segments.length == 4 && "epics".equals(segments[1]) && "subtask".equals(segments[3])) {
                try {
                    int id = Integer.parseInt(segments[2]);
                    var subtasks = taskManager.getSubTaskEpic(id);
                    if (subtasks != null) {
                        String jsonResponse = gson.toJson(subtasks);
                        sendText(exchange, jsonResponse, 200);
                    } else {
                        sendNotFound(exchange);
                    }
                } catch (NumberFormatException e) {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
            Epic epic;
            try {
                epic = gson.fromJson(body, Epic.class);
            } catch (Exception e) {
                sendText(exchange, "Ошибка: Некорректный формат JSON", 400);
                return;
            }

            try {
                taskManager.addEpic(epic);
                String jsonResponse = gson.toJson(epic);
                sendText(exchange, jsonResponse, 201);
            } catch (IllegalArgumentException e) {
                sendHasOverlaps(exchange);
            }
        }

        private void handleDelete(HttpExchange exchange, String path) throws IOException {
            String idStr = path.substring("/epics/".length());
            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                sendText(exchange, "Ошибка: Некорректный id эпика", 400);
                return;
            }

            try {
                taskManager.deleteEpic(id);
                sendText(exchange, "Сообщение: эпик с id " + id + " удален", 200);
            } catch (NoSuchElementException e) {
                sendNotFound(exchange);
            }
        }
    }

    static class HistoryHandler extends BaseHttpHandler implements HttpHandler {
        private final HistoryManager historyManager;
        private final Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        public HistoryHandler(HistoryManager historyManager) {
            this.historyManager = historyManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                var history = historyManager.getHistory();
                String jsonResponse = gson.toJson(history);
                sendText(exchange, jsonResponse, 200);
            } else {
                sendText(exchange, "Метод не поддерживается", 405);
            }
        }
    }

    static class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
        private final TaskManager taskManager;
        private final Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        public PrioritizedHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String jsonResponse = gson.toJson(prioritizedTasks);
                sendText(exchange, jsonResponse, 200);
            } else {
                sendText(exchange, "Метод не поддерживается", 405);
            }
        }
    }
}
