package ru.nsu.fit.smolyakov.sobakacloud.server.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.DefaultServlet;
import ru.nsu.fit.smolyakov.sobakacloud.aop.dto.ArgDto;
import ru.nsu.fit.smolyakov.sobakacloud.aop.dto.TaskSubmitRequestDto;
import ru.nsu.fit.smolyakov.sobakacloud.server.executor.BytesClassLoader;
import ru.nsu.fit.smolyakov.sobakacloud.server.executor.MethodExecutor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class SubmitServlet extends DefaultServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var parts = req.getParts();
        if (parts.size() != 2) {
            resp.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "only classFile and requestInfo allowed and required"
            );
            return;
        }

        byte[] requestDtoBytes = null;
        byte[] classBytes = null;

        for (var part : req.getParts()) {
            switch (part.getName()) {
                case "classFile" -> classBytes = part.getInputStream().readAllBytes();
                case "requestInfo" -> requestDtoBytes = part.getInputStream().readAllBytes();
                default -> {
                    resp.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "only classFile and requestInfo allowed and required"
                    );
                    return;
                }
            }
        }

        if (requestDtoBytes == null) {
            resp.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "classFile is null (?)"
            );
            return;
        }
        System.err.println(new String(requestDtoBytes));

        var bytesClassLoader = new BytesClassLoader();

        TaskSubmitRequestDto taskSubmitRequestDto = TaskSubmitRequestDto.deserialize(requestDtoBytes);
        Class<?> clazz = bytesClassLoader.loadClassFromBytes(classBytes);

        Class<?>[] argTypes = taskSubmitRequestDto.getArgs()
            .stream()
            .map(ArgDto::getArgType)
            .map(ArgDto.Type::getAssociatedClass)
            .toArray(Class<?>[]::new);

        Method method;
        try {
            method = clazz.getMethod(taskSubmitRequestDto.getEntryMethodName(), argTypes);
        } catch (NoSuchMethodException e) {
            resp.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "no public method named " + taskSubmitRequestDto.getEntryMethodName()
            );
            return;
        }

        if (!Modifier.isStatic(method.getModifiers())) {
            resp.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "method named " + taskSubmitRequestDto.getEntryMethodName() + " is not static"
            );
            return;
        }

        if (ArgDto.Type.fromClass(method.getReturnType()).isEmpty()) {
            resp.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "method named " + taskSubmitRequestDto.getEntryMethodName() + " has unsupported return type"
            );
            return;
        }

        Object[] args = taskSubmitRequestDto.getArgs()
            .stream()
            .map(ArgDto::getArgValueAsObject)
            .toArray(Object[]::new);

        long id = MethodExecutor.INSTANCE.submitMethod(new MethodExecutor.Task(method, args));
        resp.getOutputStream().print(String.valueOf(id));
    }
}
