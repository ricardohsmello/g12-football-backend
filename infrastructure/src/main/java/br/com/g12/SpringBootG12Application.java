package br.com.g12;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBootG12Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootG12Application.class, args);
    }
}
