package hr.lektie.mobileapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "hr.lektie.common")
public class MobileApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobileApiApplication.class, args);
    }
}
