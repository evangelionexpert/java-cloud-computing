package ru.nsu.fit.smolyakov.sobakacloud.server.executor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public enum MethodExecutor {
    INSTANCE;

    public static class Task {
        private final Method method;
        private final Object[] args;

        public Task(Method method, Object[] args) {
            this.method = Objects.requireNonNull(method);
            this.args = Objects.requireNonNull(args);
        }

        public Method getMethod() {
            return method;
        }

        public Object[] getArgs() {
            return args;
        }
    }

    public abstract static sealed class TaskResult permits TaskResult.Done, TaskResult.InProgress, TaskResult.Failed  {
        public enum Status {
            IN_PROCESS,
            DONE,
            FAILED
        };

        private final Status status;

        private TaskResult(Status status) {
            this.status = status;
        }

        public Status getStatus() {
            return status;
        }

        public static final class Done extends TaskResult {
            private final Object result;
            private final Class<?> resultClazz;

            private Done(Object result, Class<?> resultClazz) {
                super(Status.DONE);
                this.result = result;
                this.resultClazz = Objects.requireNonNull(resultClazz);
            }
        }

        public static final class InProgress extends TaskResult {
            private InProgress() {
                super(Status.IN_PROCESS);
            }
        }

        public static final class Failed extends TaskResult {
            private final Throwable cause;

            private Failed(Throwable cause) {
                super(Status.FAILED);
                this.cause = cause;
            }

            public Throwable getCause() {
                return cause;
            }
        }
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<Long, Future<TaskResult.Done>> idToTask = new HashMap<>();
    private long id = 0;

    public long submitMethod(Task req) {
        var future = executor.submit(() -> new TaskResult.Done(req.method.invoke(req.args), req.method.getReturnType()));
        idToTask.put(id++, future);
        return id;
    }

    public TaskResult getResult(long id) {
        var respFuture = idToTask.get(id);
        if (respFuture == null) {
            throw new RuntimeException("no such task"); // todo custom
        }

        if (!respFuture.isDone()) {
            return new TaskResult.InProgress();
        }

        try {
            return respFuture.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof InvocationTargetException ite) {
                return new TaskResult.Failed(ite.getTargetException());
            } else {
                return new TaskResult.Failed(e.getCause());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e); // todo а че с этим то делать
        }
    }
}
