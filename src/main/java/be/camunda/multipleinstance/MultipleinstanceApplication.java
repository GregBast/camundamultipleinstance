package be.camunda.multipleinstance;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class MultipleinstanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultipleinstanceApplication.class, args);
    }

}
