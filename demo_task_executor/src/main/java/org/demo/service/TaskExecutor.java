package org.demo.service;

import java.util.concurrent.Future;

public interface TaskExecutor {
    <T> Future<T> submitTask(Task<T> task);
}
