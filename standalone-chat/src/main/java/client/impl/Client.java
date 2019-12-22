package client.impl;

import client.UI.ClientUI;
import client.vo.Message;
import client.constant.Colors;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class Client extends Thread{

    private String username = "USERNAME";
    private Map<String, String> knownUsernames = new HashMap<String, String>();

    private Socket socket;
    private Scanner reader;
    private BufferedReader in;
    private BufferedWriter out;

    private InetSocketAddress inetSocketAddress;
    private ByteBuffer outBuffer;
    SocketChannel socketChannel;

    private String host = "localhost";

    public ClientUI ui;

    public Client() {
    }

    public void configure() throws IOException{
        socket = new Socket(host, 1488);

        reader = new Scanner(System.in);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void configureToNioMode() throws IOException{
        inetSocketAddress = new InetSocketAddress(host, 1488);

        socketChannel = SocketChannel.open(inetSocketAddress);
        reader = new Scanner(System.in);
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
        ui = new ClientUI("Client", username);

        sendHello();

        messageReader.start();
        messageWriter.start();
    }

    public void sendHello(){
        Message message = Message.enteredUserMessage(username);
        try {
            outBuffer = ByteBuffer.wrap(message.toGsonString().getBytes());
            while (outBuffer.hasRemaining()){
                socketChannel.write(outBuffer);
            }
        } catch (IOException e) {}
    }

    @SuppressWarnings("all")
    private String getColorForMessage(Message message){

        if(message.isSendFromHost()) return Colors.ANSI_BLACK_BACKGROUND + Colors.ANSI_WHITE;

        int index = Math.abs(message.getUsername().hashCode() % Colors.colorsAmount);
        
        knownUsernames.putIfAbsent(username, Colors.colors[index]);

        return knownUsernames.get(username);
    }

    private class MessageReader extends Thread {

        @Override
        public void run(){

            while(true) {
                try {

                    ByteBuffer inBuffer = ByteBuffer.allocate(10000);
                    if(socketChannel.read(inBuffer) > 0){
                        Message message = Message.fromJson(new String(inBuffer.array()));

//                        if(!message.isTechnical()) System.out.println(getColorForMessage(message) + message.toString() + Colors.ANSI_RESET);
                        if(!message.isTechnical()){
                            System.out.println("in MessageReader, sending message to ui");
                            ui.writeMessage(message);
                        }
                    }

                } catch (IOException e) {

                }
            }
        }
    }

    private class MessageWriter extends Thread {

        @Override
        public void run(){

            while (true) {
                try {

//                    Message message = new Message(username, reader.nextLine());
                    if(!ui.getLastMessage().equals("")) {
                        Message message = new Message(username, ui.getLastMessage());
                        outBuffer = ByteBuffer.wrap(message.toGsonString().getBytes());
                        System.out.println("in MessageWriter, extracting message from ui");

                        while (outBuffer.hasRemaining()) {
                            socketChannel.write(outBuffer);
                        }
                    }

                } catch (IOException e) {
                    System.out.println("Something wrong while sending message");
                    e.printStackTrace();
                }
            }
        }
    }
}
