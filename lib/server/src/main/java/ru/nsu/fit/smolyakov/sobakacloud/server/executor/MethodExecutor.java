package ru.nsu.fit.smolyakov.sobakacloud.server.executor;

import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public enum MethodExecutor {
    INSTANCE;

    private static final Logger log = Logger.getLogger(MethodExecutor.class.getName());

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
        public static final class Done extends TaskResult {
            private final Object result;
            private final Class<?> resultClazz;

            private Done(Object result, Class<?> resultClazz) {
                this.result = result;
                this.resultClazz = Objects.requireNonNull(resultClazz);
            }

            public Object getResult() {
                return result;
            }

            public Class<?> getResultClazz() {
                return resultClazz;
            }
        }

        public static final class InProgress extends TaskResult {
            private InProgress() {
            }
        }

        public static final class Failed extends TaskResult {
            private final Throwable cause;

            private Failed(Throwable cause) {
                this.cause = cause;
            }

            public Throwable getCause() {
                return cause;
            }
        }
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<Long, Future<TaskResult>> idToTask = new HashMap<>();
    private long id = 0;

    private TaskResult executorTask(Task req, long id) throws InvocationTargetException, IllegalAccessException {
        log.info("(id %d) new task: %s".formatted(id, req.method.toGenericString()));
        Object res;

        try {
            res = req.method.invoke(null, req.args);
        } catch (InvocationTargetException e){
            log.info(
                "(id %d) underlying method exception: %s (%s)"
                    .formatted(id, e.getTargetException().toString(), e.getTargetException().getMessage()));
            return new TaskResult.Failed(e.getCause());
        } catch (Exception e) {
            log.info("(id %d) exception: %s".formatted(id, e.toString()));
            return new TaskResult.Failed(e);
        }

        log.info("(id %d) task done".formatted(id));
        return new TaskResult.Done(res, req.method.getReturnType());
    }

    public long submitMethod(Task req) {
        id++;
        var future = executor.submit(() -> executorTask(req, id));
        idToTask.put(id, future);
        return id;
    }

    public Optional<TaskResult> getResult(long id) {
        var respFuture = idToTask.get(id);
        if (respFuture == null) {
            return Optional.empty();
        }

        if (!respFuture.isDone()) {
            return Optional.of(new TaskResult.InProgress());
        }

        try {
            return Optional.of(respFuture.get());
        } catch (InterruptedException ignored) {
            return Optional.empty();
        } catch (ExecutionException e) {
            return Optional.of(new TaskResult.Failed(e));
        }
    }
}
