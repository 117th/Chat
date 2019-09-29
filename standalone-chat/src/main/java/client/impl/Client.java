package client.impl;

import client.vo.Message;
import client.constant.Colors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import server.ServerRunner;

import java.io.*;
import java.net.Socket;
import java.util.*;

@Component
public class Client extends Thread{

    private String username = "USERNAME";
    private Map<String, String> knownUsernames = new HashMap<String, String>();

    private Socket socket;
    private Scanner reader;
    private BufferedReader in;
    private BufferedWriter out;

    private String host = "localhost";

    @Autowired
    private ApplicationContext context;

    public Client() {
    }

    public void configure() throws IOException{
        socket = new Socket(host, 1488);

        reader = new Scanner(System.in);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void run(){
        MessageReader messageReader = new MessageReader();
        MessageWriter messageWriter = new MessageWriter();

        sendHello();

        messageReader.start();
        messageWriter.start();

        System.out.println("Started writer and reader");
    }

    public void sendHello(){
        Message message = new Message("HOST",username + " has entered group\n");
        try {
            out.write(message.toString());
            out.flush();
        } catch (IOException e) {}
    }

    private String getColorForUser(String username){

        if(username.equals("HOST")) return Colors.ANSI_BLACK_BACKGROUND + Colors.ANSI_WHITE;

        int index = Math.abs(username.hashCode() % Colors.colorsAmount);

        knownUsernames.putIfAbsent(username, Colors.colors[index]);

        return knownUsernames.get(username);
    }

    private class MessageReader extends Thread {

        @Override
        public void run(){

            while(true) {
                try {
                    Message message = new Message(in.readLine());

                    if(message.getUsername().equals("SERVER_SHUTDOWN")){
                        handleServerStartRequest(message);
                    } else if (message.getUsername().equals("SERVER_TRANSFER")) {
                        handleServerAddressChange(message);
                    } else {
                        System.out.println(getColorForUser(message.getUsername()) + message.toString() + Colors.ANSI_RESET);
                    }
                } catch (IOException e) {

                }
            }
        }
    }

    private void handleServerStartRequest(Message transferMessage){

        System.out.println("Got server shutdown message. Server gonna start on this machine");

        ServerRunner serverRunner = context.getBean(ServerRunner.class);

        serverRunner.transferHistory(transferMessage.getBody());
        serverRunner.start();

    }

    public void handleServerAddressChange(Message newAddressMessage) throws IOException{
        host = newAddressMessage.getBody();
        configure();
    }

    private class MessageWriter extends Thread {

        @Override
        public void run(){

            while (true) {
                try {
                    Message message = new Message(username, reader.nextLine());
                    if(message.getBody().equals("STOP_SERVER")) System.exit(0);
                    out.write(message.toString() + "\n");
                    out.flush();
                } catch (IOException e) {
                    System.out.println("Something wrong while sending message");
                    e.printStackTrace();
                }
            }
        }
    }

}
