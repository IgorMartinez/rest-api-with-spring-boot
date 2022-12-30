package br.com.igormartinez.restapiwithspringboot.exceptions.handler;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import br.com.igormartinez.restapiwithspringboot.exceptions.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc) throws IOException {
        response.setContentType("application/json");
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), "Not authorized", request.getRequestURI());
        response.getWriter().write(exceptionResponse.toJsonString());
        response.setStatus(401);
    }
}