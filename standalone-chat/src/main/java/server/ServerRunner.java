package server;

import server.listener.Listener;
import client.vo.Message;
import org.springframework.stereotype.Component;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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

        Message closeMessage = new Message("SERVER_SHUTDOWN", collection.toString());

        listeners.getLast().send(closeMessage.toString());
    }
}
