package ru.nsu.fit.smolyakov.sobakacloud.server.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.DefaultServlet;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.ArgDto;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.TaskSubmitRequestDto;
import ru.nsu.fit.smolyakov.sobakacloud.server.executor.BytesClassLoader;
import ru.nsu.fit.smolyakov.sobakacloud.server.executor.MethodExecutor;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

public class ResultServlet extends DefaultServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idString = req.getParameter("id");
        if (idString == null) {
            resp.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "no id specified in query parameters. type something like 'GET /compute/result?id=100500'"
            );
            return;
        }
        long id;
        try {
            id = Long.parseLong(idString);
        } catch (NumberFormatException e) {
            resp.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "id must be an integer value"
            );
            return;
        }
        var resultOptional = MethodExecutor.INSTANCE.getResult(id);

        if (resultOptional.isPresent()) {
            var result = resultOptional.get();
            if (result instanceof MethodExecutor.TaskResult.Done done) {
                System.err.println("saf");
                ArgDto.Type.fromClass(done.getResultClazz()).ifPresentOrElse(
                    (argDto) -> {},
                    () -> {
                        try {
                            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "unknown result class, can't serialize");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                );
            } else {
                System.err.println(((MethodExecutor.TaskResult.Failed) result).getCause().getMessage());
            }
        } else {
            resp.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "no task with such id"
            );
        }
    }
}
