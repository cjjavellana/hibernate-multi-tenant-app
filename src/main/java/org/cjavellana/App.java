package org.cjavellana;

import org.cjavellana.db.TenantContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "org.cjavellana")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(new Object[]{App.class}, args);
    }

}
