package drhm.xyz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ProfileExternalApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ProfileExternalApplication.class, args);
        System.out.println("OK");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ProfileExternalApplication.class);
    }

    @RestController
    @RequestMapping("/dr")
    public class ProfileExternalController {

        @GetMapping("/get")
        public String profileTest() {
            return "挣脱枷锁";
        }

    }

}
