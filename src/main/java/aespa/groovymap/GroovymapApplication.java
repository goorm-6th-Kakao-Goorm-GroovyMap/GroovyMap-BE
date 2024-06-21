package aespa.groovymap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GroovymapApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroovymapApplication.class, args);
    }

}
