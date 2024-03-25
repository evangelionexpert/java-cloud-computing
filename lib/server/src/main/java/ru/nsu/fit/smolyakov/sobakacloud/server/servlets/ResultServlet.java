package ru.nsu.fit.smolyakov.sobakacloud.server.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.DefaultServlet;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.ArgDto;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.TaskResultResponseDto;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.TaskSubmitRequestDto;
import ru.nsu.fit.smolyakov.sobakacloud.server.exceptions.SobakaExecutionException;
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
            var res = resultOptional.get();
            byte[] respDtoBytes = new byte[0];

            if (res instanceof MethodExecutor.TaskResult.Done done) {
                var argDto = ArgDto.Type
                    .fromClass(done.getResultClazz())
                    .get()
                    .createDto(done.getResult());

                respDtoBytes = new TaskResultResponseDto.Success(argDto)
                    .serialize();
            } else if (res instanceof MethodExecutor.TaskResult.Failed failed) {
                respDtoBytes = new TaskResultResponseDto.Failure(
                    new SobakaExecutionException(
                        failed.getCause().toString()
                    )).serialize();
            } else {
                respDtoBytes = new TaskResultResponseDto.InProgress().serialize();
            }
            resp.getOutputStream().write(respDtoBytes);
            System.err.println(new String(respDtoBytes));
        } else {
            resp.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "no task with such id"
            );
        }
    }
}
