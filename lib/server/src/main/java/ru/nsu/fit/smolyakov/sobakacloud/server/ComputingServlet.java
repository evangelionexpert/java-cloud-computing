package ru.nsu.fit.smolyakov.sobakacloud.server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.DefaultServlet;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.ArgDto;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.RequestDto;
import ru.nsu.fit.smolyakov.sobakacloud.server.executor.BytesClassLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ComputingServlet extends DefaultServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var parts = req.getParts();
        if (parts.size() != 2) {
            resp.sendError(400, "only classFile and requestInfo allowed and required");
            return;
        }

        byte[] requestDtoBytes = null;
        byte[] classBytes = null;

        for (var part : req.getParts()) {
            switch (part.getName()) {
                case "classFile" -> classBytes = part.getInputStream().readAllBytes();
                case "requestInfo" -> requestDtoBytes = part.getInputStream().readAllBytes();
                default -> {
                    resp.sendError(400, "only classFile and requestInfo allowed and required");
                    return;
                }
            }
        }

        var bytesClassLoader = new BytesClassLoader();

        RequestDto requestDto = RequestDto.deserialize(requestDtoBytes);
        Class<?> clazz = bytesClassLoader.loadClassFromBytes(classBytes);

        Class<?>[] argTypes = requestDto.getArgs()
            .stream()
            .map(ArgDto::getArgValueClass)
            .toArray(Class<?>[]::new);

        Method method = null;
        try {
            method = clazz.getMethod(requestDto.getEntryMethodName(), argTypes);
        } catch (NoSuchMethodException e) {
            resp.sendError(400, "no public method called " + requestDto.getEntryMethodName());
            return;
        }

        Object[] args = requestDto.getArgs()
            .stream()
            .map(ArgDto::getArgValueAsObject)
            .toArray(Object[]::new);

        if (!Modifier.isStatic(method.getModifiers())) {
            resp.sendError(400, "method named " + requestDto.getEntryMethodName() + " is not static");
            return;
        }

        Object res = null;
//        method.getReturnType();
        try {
            res = method.invoke(null, args);
        } catch (IllegalAccessException e) {
            resp.sendError(400, "illegal access");
            return;
        } catch (InvocationTargetException e) {
            resp.sendError(400, "invocation target exception");
            return;
        }

        ArgDto argDto = ArgDto.argDtoFromValue(res, method.getReturnType());
    }
}
