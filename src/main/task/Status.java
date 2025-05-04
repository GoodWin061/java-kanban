package main.task;

public enum Status {
    NEW("Новая задача"),
    IN_PROGRESS("В процессе выполнения"),
    DONE("Задача завершена");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
