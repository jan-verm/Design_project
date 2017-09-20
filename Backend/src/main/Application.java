package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import config.Config;

/**
 * Main class. This launches the application.
 * 
 * @author Jan Vermeulen
 */
@SpringBootApplication
public class Application {
    
	public static void main(String[] args) {		
		SpringApplication app = new SpringApplication(Config.class);
		app.run(args);
	}
}