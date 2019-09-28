package client.vo;

import java.sql.Timestamp;
import java.util.Date;

public class Message {

    private String username;
    private Timestamp date;
    private String body;

    public Message(String user, String body){
        username = user;
        this.body = body;
        date = new Timestamp(new Date().getTime());
    }

    public Message(String msg){

        String[] parse = msg.split(" ");

        username = parse[2].replaceAll("\\[", "").replace("]", "").replace(":","");
        date = Timestamp.valueOf(parse[0] + " " + parse[1]);
        body = "";

        for(int i = 3; i < parse.length; i++) body += parse[i] + " ";
    }

    public String getUsername() {
        return username;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();

        return stringBuilder.append(date).append(" ").append("[").append(username).append("]").append(": ").append(body).toString();
    }
}
