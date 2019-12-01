package server;

import server.listener.Listener;
import client.vo.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

@Component
public class ServerRunner extends Thread{
    public static final int PORT = 1488;

    public static LinkedList<Listener> listeners = new LinkedList<Listener>();
    public static List<Message> history = new ArrayList<Message>();
    private Selector selector;

    @Override
    public void run() {
        ServerSocket serverSocket;

        String mode = "nb";

        try{

            if(mode.equals("nb")){
                selector = Selector.open();

                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", PORT);

                serverSocketChannel.bind(inetSocketAddress);
                serverSocketChannel.configureBlocking(false);

                SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

                while(true){
                    if(selector.select() > 0){
                        handleKeys(selector.selectedKeys(), serverSocketChannel);
                    }
                }
            }

            if(!mode.equals("nb")) {
                serverSocket = new ServerSocket(PORT);

                while (true) {
                    Socket socket = serverSocket.accept();
                    listeners.add(new Listener(socket));
                    System.out.println("Got new client. Client count: " + listeners.size());
            }

        }
        } catch (Exception e) {
            System.out.println("Runner failed");
            e.printStackTrace();
        }
    }

    private void handleKeys(Collection<SelectionKey> selectedKeys, ServerSocketChannel serverSocketChannel) throws IOException{
        final Iterator<SelectionKey> selectionKeyIterator = selectedKeys.iterator();

        while(selectionKeyIterator.hasNext()){
            SelectionKey selectionKey = selectionKeyIterator.next();

            if(selectionKey.isAcceptable()){
                acceptClientSocket(selectionKey, serverSocketChannel);
            } else if(selectionKey.isReadable()){
                readRequest(selectionKey);
            } else {
                System.out.println("Invalid key");
            }

            selectionKeyIterator.remove();
        }
    }

    private void acceptClientSocket(SelectionKey key, ServerSocketChannel serverSocket) throws IOException {

        final SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(key.selector(), SelectionKey.OP_READ);

        System.out.println("Accepted connection from client");
    }

    private void readRequest(SelectionKey key) throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(10000);

        final int bytesRead = client.read(buffer);

        if (bytesRead == -1) {
            client.close();
        } else {
            String json = new String(buffer.array());

            for(SelectionKey selectionKey : selector.keys()){
                if(!selectionKey.equals(key) && selectionKey.channel() instanceof SocketChannel) {
                        SocketChannel clientSendTo = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.wrap(json.getBytes());
                        clientSendTo.write(byteBuffer);
                }
            }
        }

    }


    public void sendCloseMessage(){

    }
}
