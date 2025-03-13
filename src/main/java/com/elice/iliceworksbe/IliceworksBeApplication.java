package com.elice.iliceworksbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IliceworksBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(IliceworksBeApplication.class, args);
    }

}
