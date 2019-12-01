package client.vo;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.sql.Timestamp;
import java.util.Date;

public class Message {

    private boolean isTechnical;
    private boolean isSendFromHost;
    private String username;
    private Timestamp date;
    private String body;

    public Message(){
        date = new Timestamp(new Date().getTime());
    }

    public Message(String username, String body){
        this.username = username;
        this.body = body;
        date = new Timestamp(new Date().getTime());
        isTechnical = false;
    }

    public static Message enteredUserMessage(String username){
        Message message = new Message();

        message.isTechnical = false;
        message.isSendFromHost = true;
        message.username = "HOST";
        message.body = username + " has entered the group";

        return message;
    }

    public String getUsername() {
        return username;
    }

    public boolean isTechnical() {
        return isTechnical;
    }

    public boolean isSendFromHost() {
        return isSendFromHost;
    }

    public String toGsonString(){
        Gson gson = new Gson();

        return gson.toJson(this) + "\n";
    }

    public static Message fromJson(String json){
        Gson gson = new Gson();

        JsonReader malformedReader = new JsonReader(new StringReader(json));
        malformedReader.setLenient(true);

        return gson.fromJson(malformedReader, Message.class);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        return stringBuilder.append("[").append(date).append("] ").append(username).append(": ").append(body).toString();
    }
}
