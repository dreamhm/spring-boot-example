package drhm.xyz;

import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ProfileApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ProfileApplication.class, args);
        System.out.println("OK");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ProfileApplication.class);
    }

    @RestController
    @RequestMapping("/dr")
    public class ProfileController{

        @GetMapping("/get")
        public String profileTest(){
            return "挣脱枷锁";
        }

    }
}
