package client.impl;

import client.constant.Colors;
import org.springframework.stereotype.Component;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Component
public class NetworkScanner {

    public static List<String> scan() {

        List<String> localIps = new ArrayList<String>();

        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            while(nis.hasMoreElements())
            {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration ias = ni.getInetAddresses();
                while (ias.hasMoreElements())
                {
                    InetAddress ia = (InetAddress) ias.nextElement();
                    localIps.add(ia.getHostName());
                }
            }

            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("127.0.0.1"), 10002);
            localIps.add(Colors.ANSI_RED + "LOCALHOST IS: " + socket.getLocalAddress().getHostAddress() + Colors.ANSI_RESET);
        } catch (Exception e) {}

        return localIps;
    }

    public static void printPossibleServers(){
        List<String> ips = scan();

        System.out.println("Local network addresses:");
        for(String ip : ips) System.out.println(ip);
        System.out.println();
    }

}
