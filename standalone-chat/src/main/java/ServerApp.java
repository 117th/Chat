import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import server.ServerRunner;

@Configuration
@ComponentScan
public class ServerApp {
    public static void main(String[] args){
        ApplicationContext context = new AnnotationConfigApplicationContext("server");

        ServerRunner serverRunner = context.getBean(ServerRunner.class);

        serverRunner.start();

    }
}
