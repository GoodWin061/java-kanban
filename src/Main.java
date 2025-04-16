public class Main {

    public static void main(String[] args) {
        int idCounter = 0;
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
        taskManager.addTask("Задача1", "Описание 1");
        taskManager.addTask("Задача2", "Описание 1");
        taskManager.viewTasks();


        taskManager.addEpic("эпик1", "описание эпика1");
        taskManager.viewEpics();

        taskManager.addSubTask("подзадача1", "описание подзадачи1", 2);
        taskManager.addSubTask("подзадача2", "описание подзадачи2", 2);
        taskManager.addSubTask("подзадача3", "описание подзадачи3", 2);
        taskManager.viewSubTasks();


    }
}
