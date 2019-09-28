package server.listener;

import client.constant.Colors;
import client.vo.Message;
import server.ServerRunner;

import java.io.*;
import java.net.Socket;

public class Listener extends Thread{

    private Socket socket;

    private BufferedReader in;
    private BufferedWriter out;

    private String username;

    public Listener(Socket socket) throws IOException {
        this.socket = socket;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    @Override
    public void run(){

        for(Message message : ServerRunner.history){
            this.send(message.toString());
        }

        Message message;

        while (true) {

            try {

                message = new Message(in.readLine());

                if(username == null && message.getUsername().equals("HOST")) username = message.getBody().split(" ")[0];

                ServerRunner.history.add(message);

                for(Listener listener : ServerRunner.listeners){
                    if(!listener.equals(this)) listener.send(message.toString());
                }
            } catch (Exception e) {
                System.out.println("Listener failed. Looks like one of the clients is closed");
                for(Listener listener : ServerRunner.listeners){
                    message = new Message("HOST",username + " has left group");
                    if(!listener.equals(this)) listener.send(message.toString());
                }
                ServerRunner.listeners.remove(this);
                this.stop();
            }
        }
    }

    public void send(String msg){
        try{
            out.write(msg + "\n");
            out.flush();
        } catch (IOException e) {
            System.out.println("Fail send");
        }
    }

    public String getIp(){
        return socket.getInetAddress().getHostAddress();
    }
}
