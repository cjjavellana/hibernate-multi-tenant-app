package org.cjavellana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.cjavellana")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(new Object[]{App.class}, args);
    }

}
