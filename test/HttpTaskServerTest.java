import main.history.InMemoryHistoryManager;
import main.manager.InMemoryTaskManager;
import main.task.*;
import main.http.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private static final int PORT = 8080;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        server = new HttpTaskServer(new InMemoryTaskManager(), new InMemoryHistoryManager());
        server.start();
        client = HttpClient.newHttpClient();

        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testGetEmptyTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testPostAndGetTask() throws IOException, InterruptedException {
        String jsonTask = """
                {                   
                      "title": "Пример задачи",
                       "description": "Описание задачи",
                       "duration": "PT1H",
                       "startTime": "2024-06-10T10:00:00"
                }
                """;

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode());
        System.out.println("Response body: " + postResponse.body());
        System.out.println("Response code: " + postResponse.statusCode());
        assertTrue(postResponse.body().contains("Пример задачи"));

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/tasks/0"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().contains("Пример задачи"));
    }

    @Test
    void shouldCreateAndGetSubTask() throws Exception {
        SubTask subTask = new SubTask(
                null,
                "Тестовая подзадача",
                "Описание подзадачи",
                Duration.ofHours(1),
                LocalDateTime.of(2024, 6, 10, 10, 0),
                1
        );

        String json = gson.toJson(subTask);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, postResponse.statusCode());

        SubTask createdSubTask = gson.fromJson(postResponse.body(), SubTask.class);

        assertNotNull(createdSubTask.getId(), "Id подзадачи должен быть присвоен сервером");
        assertEquals(subTask.getTitle(), createdSubTask.getTitle());
        assertEquals(subTask.getEpicId(), createdSubTask.getEpicId());

        int createdId = createdSubTask.getId();

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/subtasks/" + createdId))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());

        SubTask fetchedSubTask = gson.fromJson(getResponse.body(), SubTask.class);

        assertEquals(createdId, fetchedSubTask.getId());
        assertEquals(subTask.getTitle(), fetchedSubTask.getTitle());
        assertEquals(subTask.getEpicId(), fetchedSubTask.getEpicId());
    }


    @Test
    void testGetHistory() throws IOException, InterruptedException {
        // Создаем и получаем задачу, чтобы она попала в историю
        String taskJson = """
                {
                      "title": "Пример задачи",
                       "description": "Описание задачи",
                       "duration": "PT1H",
                       "startTime": "2024-06-10T10:00:00"
                }
                """;

        client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build(), HttpResponse.BodyHandlers.ofString());

        client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/tasks/0"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        HttpRequest historyRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/history"))
                .GET()
                .build();

        HttpResponse<String> historyResponse = client.send(historyRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response body: " + historyResponse.body());
        System.out.println("Response code: " + historyResponse.statusCode());
        assertEquals(200, historyResponse.statusCode());
        assertTrue(historyResponse.body().contains("Пример задачи"));
    }

    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        String jsonTask1 = """
        {
            "title": "Задача 1",
            "description": "Описание задачи 1",
            "duration": "PT30M",
            "startTime": "2024-06-10T09:00:00"
        }
        """;

        HttpRequest postRequest1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask1))
                .build();

        HttpResponse<String> postResponse1 = client.send(postRequest1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse1.statusCode());

        String jsonTask2 = """
        {
            "title": "Задача 2",
            "description": "Описание задачи 2",
            "duration": "PT45M",
            "startTime": "2024-06-10T10:00:00"
        }
        """;

        HttpRequest postRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask2))
                .build();

        HttpResponse<String> postResponse2 = client.send(postRequest2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse2.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        System.out.println("Prioritized tasks response: " + responseBody);

        assertNotNull(responseBody);
        assertTrue(responseBody.startsWith("[") && responseBody.endsWith("]"));

        Gson gsonWithAdapters = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        Task[] tasks = gsonWithAdapters.fromJson(responseBody, Task[].class);
        assertNotNull(tasks);

        assertTrue(tasks.length >= 2);
    }
}
