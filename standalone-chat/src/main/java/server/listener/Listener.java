package server.listener;

import client.constant.Colors;
import client.vo.Message;
import com.google.gson.Gson;
import server.ServerRunner;

import java.io.*;
import java.net.Socket;

public class Listener extends Thread{

    private Socket socket;

    private BufferedReader in;
    private BufferedWriter out;

    private String username;

    private Gson gson = new Gson();

    public Listener(Socket socket) throws IOException {
        this.socket = socket;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    @Override
    public void run(){

        for(Message message : ServerRunner.history){
            this.send(message.toGsonString());
        }

        while (true) {

            try {

                String inline = in.readLine();

                Message message = gson.fromJson(inline, Message.class);

                ServerRunner.history.add(message);

                for(Listener listener : ServerRunner.listeners){
                    if(!listener.equals(this)) listener.send(message.toGsonString());
                }

            } catch (Exception e) {
                System.out.println("Listener failed. Looks like one of the clients is closed");
            }
        }
    }

    public void send(String msg){
        try{
            out.write(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println("Fail send");
        }
    }
}
