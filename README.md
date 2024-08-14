# Task Executor Service

The Task Executor Service is a Java-based asynchronous task execution framework that allows tasks to be submitted, executed, and managed with constraints on concurrency and task grouping. It ensures that tasks are executed in the order they are submitted and supports concurrent execution with respect to specified constraints.

## Features

1. **Concurrent Task Submission**: Submit tasks without blocking the submitter.
2. **Asynchronous and Concurrent Execution**: Execute tasks asynchronously with a maximum allowed concurrency.
3. **Result Retrieval**: Retrieve task results using `Future` objects.
4. **Ordered Execution**: Maintain the order of task submission.
5. **Task Group Constraints**: Tasks within the same group are executed sequentially.

## Components

### TaskExecutor Interface

Defines the main method for submitting tasks:

```java
import java.util.concurrent.Future;

public interface TaskExecutor {
    
    <T> Future<T> submit(Task<T> task, String taskGroup);
}
