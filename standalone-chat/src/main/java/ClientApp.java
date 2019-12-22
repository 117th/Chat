import client.impl.Client;
import client.impl.NetworkScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import server.ServerRunner;

import java.io.IOException;
import java.util.Scanner;

@Configuration
@ComponentScan
public class ClientApp {

    public static ServerRunner serverRunner;

    public static void main(String[] args) throws IOException{
        //TODO: clean this mess up
        ApplicationContext context = new AnnotationConfigApplicationContext("client", "server");

        Scanner in = new Scanner(System.in);

        Client client = context.getBean(Client.class);


        NetworkScanner.printPossibleServers();

        System.out.print("Your username: ");
        client.setUsername(in.nextLine());

        String host;
        while (true) {
            try {
                System.out.print("Host: ");
                host = in.nextLine();
                client.setHost(StringUtils.hasText(host) ? host : "localhost");
                client.configureToNioMode();
                break;
            } catch (IOException e) {
                System.out.println("No such host. Enter valid host or create local server");
                System.out.print("Do you wanna start local server? y/n: ");
                if(in.nextLine().equals("y")){
                    serverRunner = context.getBean(ServerRunner.class);
                    serverRunner.start();
                }
            }
        }

        client.configureToNioMode();

        client.start();

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                System.out.println("Moving server to different machine");
                serverRunner.sendCloseMessage();
                try{
                    Thread.sleep(10000);
                } catch (InterruptedException e) {}
            }
        });
    }
}