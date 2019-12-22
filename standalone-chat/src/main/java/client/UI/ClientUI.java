package client.UI;

import client.vo.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ClientUI extends JFrame implements ActionListener {
    private JTextField textField;
    private JTextArea textArea;
    private JButton inputButton;

    private String lastMessage;
    private String userName;

    public ClientUI(String title, String userName) {
        super(title);

        this.userName = userName;

        Toolkit toolKit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolKit.getScreenSize();
        setBounds(dimension.width / 2 - 400, dimension.height / 2 - 300, 800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setState(Frame.NORMAL);

        textArea = new JTextArea(40, 40);
        textArea.setEditable(false);
        textField = new JTextField(40);
        inputButton = new JButton("Input");
        inputButton.addActionListener(this);
        inputButton.setActionCommand("sendMessage");

        setLayout(new BorderLayout());

        add(textArea, BorderLayout.CENTER);
        add(textField, BorderLayout.SOUTH);
        add(inputButton, BorderLayout.EAST);
    }

    public String getLastMessage() {
        String toReturn = lastMessage;
        lastMessage = "";
        return toReturn;
    }

    public void writeMessage(Message message){
        if(!message.getUsername().equals(this.userName))
            textArea.setText(textArea.getText() + message.getUsername() + ": " + message.toString() + "\n");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("sendMessage")) {
            lastMessage = textField.getText();
            textField.setText("");
            textArea.setText(textArea.getText() + userName + ": " + lastMessage + "\n");
        }
    }
}

