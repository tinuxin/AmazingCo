package com.amazingco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    @GetMapping("/")
    public String helloworld() {
        return "Hello world";
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}