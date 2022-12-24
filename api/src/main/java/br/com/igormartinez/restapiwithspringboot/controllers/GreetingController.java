package br.com.igormartinez.restapiwithspringboot.controllers;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.restapiwithspringboot.utils.Greeting;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Greeting", description = "Endpoint to receive a greeting")
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/api/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name){
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
}
