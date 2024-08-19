package org.demo.service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class TaskExecutorService implements TaskExecutor {
    private final int maxConcurrency;
    private final ExecutorService executor;
    private final Map<TaskGroup, Semaphore> groupSemaphores;
    private final Queue<Task<?>> taskQueue;
    private final Map<UUID, Future<?>> futures;

    public TaskExecutorService(int maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
        this.executor = Executors.newFixedThreadPool(maxConcurrency);
        this.groupSemaphores = new ConcurrentHashMap<>();
        this.taskQueue = new LinkedList<>();
        this.futures = new ConcurrentHashMap<>();
    }

    @Override
    public <T> Future<T> submitTask(Task<T> task) {
        synchronized (taskQueue) {
            taskQueue.add(task);
            if (taskQueue.size() == 1) {
                processQueue();
            }
        }
        return (Future<T>) futures.get(task.taskUUID());
    }

    private void processQueue() {
        Task<?> task;
        synchronized (taskQueue) {
            task = taskQueue.poll();
        }
        if (task != null) {
            TaskGroup group = task.taskGroup();
            Semaphore semaphore = groupSemaphores.computeIfAbsent(group, g -> new Semaphore(1));
            try {
                semaphore.acquire();
                Future<?> future = executor.submit(() -> {
                    try {
                        return task.taskAction().call();
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    } finally {
                        semaphore.release();
                        synchronized (taskQueue) {
                            if (!taskQueue.isEmpty()) {
                                processQueue();
                            }
                        }
                    }
                });
                futures.put(task.taskUUID(), future);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
