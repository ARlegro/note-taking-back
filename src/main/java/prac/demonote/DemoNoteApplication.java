package prac.demonote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DemoNoteApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoNoteApplication.class, args);
  }

}
