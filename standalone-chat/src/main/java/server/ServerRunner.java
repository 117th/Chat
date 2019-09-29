package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import server.listener.Listener;
import client.vo.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class ServerRunner extends Thread{
    public static final int PORT = 1488;
    public static LinkedList<Listener> listeners = new LinkedList<Listener>();

    public static List<Message> history = new ArrayList<Message>();

    @Override
    public void run() {
        ServerSocket serverSocket;
        try{

        serverSocket = new ServerSocket(PORT);

            while(true) {
                Socket socket = serverSocket.accept();
                listeners.add(new Listener(socket));
                System.out.println("Got new client. Client count: " + listeners.size());
            }
        } catch (Exception e) {
            System.out.println("Runner failed");
        }
    }

    public void sendCloseMessage(){
        StringBuilder collection = new StringBuilder();

        for(Message message : history){
            collection.append(message.toString());
        }

        System.out.println("History message created. Sending request to start new broker");

        Message closeMessage = new Message("SERVER_SHUTDOWN", collection.toString());

        listeners.getLast().send(closeMessage.toString());

        String newAddress = listeners.getLast().getIp();

        closeMessage = new Message("SERVER_TRANSFER", newAddress);

        System.out.println("Gonna start server at " + newAddress);

        for(Listener listener : listeners){
            listener.send(closeMessage.toString());
        }

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {}
    }

    public void transferHistory(String historyCollection){
        List<String> historyAsStrigs = Arrays.asList(historyCollection.split(";"));

        System.out.println("Transfering history, " + historyAsStrigs.size() + " messages");

        for(String messageString : historyAsStrigs) history.add(new Message(messageString + "\n"));
    }
}
