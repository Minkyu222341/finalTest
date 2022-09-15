package sparta.seed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class SeedApplication {

  public static final String APPLICATION_LOCATIONS = "spring.config.location="
          + "classpath:application.properties,"
          + "/app/config/springboot-webservice/application.yml";

  public static void main(String[] args) {
    new SpringApplicationBuilder(SeedApplication.class)
            .properties(APPLICATION_LOCATIONS)
            .run(args);

  }
//
//  public static void main(String[] args) {
//    SpringApplication.run(SeedApplication.class, args);
//  }

}
