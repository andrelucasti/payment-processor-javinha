package io.andrelucas.Rinha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RinhaApplication {

	public static void main(String[] args) {
		SpringApplication.run(RinhaApplication.class, args);
	}

}
