package com.ldap.auth.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Yuriy Tumakha
 */
@SpringBootApplication
@PropertySource("classpath:application.properties")
public class WebApplication {

    private static final String PID_FILE_NAME = "auth-agent.pid";

    public static void main(String[] args) throws Exception {
        SpringApplication springApplication = new SpringApplication(WebApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter(PID_FILE_NAME));
        springApplication.run(args);
    }

}