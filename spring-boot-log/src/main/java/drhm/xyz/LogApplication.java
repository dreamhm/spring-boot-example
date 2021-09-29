package drhm.xyz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class LogApplication extends SpringBootServletInitializer {

    /*public static void main(String[] args) {
        SpringApplication.run(LogApplication.class, args);
        System.out.println("OK");
    }*/

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(LogApplication.class);
    }


    @RestController
    @RequestMapping("/dr")
    public class LogController{

        @GetMapping("/get")
        public String profileTest(){
            return "挣脱枷锁";
        }

    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(LogApplication.class);
        //调用 sl4j 的 info() 方法，而非调用 logback 的方法
        logger.info("Hello World");
    }
}
