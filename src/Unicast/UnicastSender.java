package Unicast;

import java.io.IOException;
import java.net.*;

public class UnicastSender extends Thread{
    private  DatagramSocket socket;
    private InetAddress host;
    private int serverPort;
    private String message;
    private String ownPort;
    public UnicastSender(String msg, int _port, int _ownPort){
        this.message = msg;
        this.ownPort = "" + _ownPort;
        this.serverPort = _port;
        try{
            this.socket = new DatagramSocket();
            this.host = InetAddress.getByName("localhost");
        } catch (SocketException | UnknownHostException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(){
        try {
            byte[] messageBuffer = message.getBytes();
            DatagramPacket request = new DatagramPacket(messageBuffer, messageBuffer.length, host, serverPort);
            socket.send(request);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
