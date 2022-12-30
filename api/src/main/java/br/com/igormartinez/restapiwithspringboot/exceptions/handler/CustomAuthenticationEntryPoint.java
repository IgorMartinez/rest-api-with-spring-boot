package br.com.igormartinez.restapiwithspringboot.exceptions.handler;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import br.com.igormartinez.restapiwithspringboot.exceptions.ExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), "Authentication required", request.getRequestURI());
        response.getWriter().write(exceptionResponse.toJsonString());
        response.setStatus(403);
    }

}
