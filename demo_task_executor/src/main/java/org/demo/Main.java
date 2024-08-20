package org.demo;

import org.demo.service.Task;
import org.demo.service.TaskExecutorService;
import org.demo.service.TaskGroup;
import org.demo.service.TaskType;
import java.util.UUID;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws Exception {
        // Initialize the TaskExecutorService with a maximum of 2 concurrent tasks
        TaskExecutorService executorService = new TaskExecutorService(2);

        try {
            // Define TaskGroups
            TaskGroup group1 = new TaskGroup(UUID.randomUUID());
            TaskGroup group2 = new TaskGroup(UUID.randomUUID());

            // Create tasks
            Task<Integer> task1 = new Task<>(UUID.randomUUID(), group1, TaskType.READ, () -> {
                Thread.sleep(500); // Simulate some work
                return 1;
            });
            Task<Integer> task2 = new Task<>(UUID.randomUUID(), group1, TaskType.READ, () -> 2);
            Task<Integer> task3 = new Task<>(UUID.randomUUID(), group2, TaskType.WRITE, () -> {
                Thread.sleep(300); // Simulate some work
                return 3;
            });
            Task<Integer> task4 = new Task<>(UUID.randomUUID(), group2, TaskType.WRITE, () -> 4);

            // Submit tasks
            Future<Integer> future1 = executorService.submitTask(task1);
            Future<Integer> future2 = executorService.submitTask(task2);
            Future<Integer> future3 = executorService.submitTask(task3);
            Future<Integer> future4 = executorService.submitTask(task4);

            // Retrieve and print results

            System.out.println("Task 1 result: " + future1.get());
            System.out.println("Task 2 result: " + future2.get());
            System.out.println("Task 3 result: " + future3.get());
            System.out.println("Task 4 result: " + future4.get());
        } catch (InterruptedException e) {
            throw new InterruptedException(e.getMessage());
        } finally {
            // Shutdown the executor service
            executorService.shutdown();
        }
    }
}
