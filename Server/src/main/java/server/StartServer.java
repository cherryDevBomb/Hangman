package server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class StartServer {

    public static void main(String[] args) {

        System.setProperty("java.rmi.server.hostname", "localhost");
        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:server-spring.xml");
    }
}
