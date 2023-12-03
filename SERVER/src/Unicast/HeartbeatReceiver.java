package Unicast;

import Node.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.UUID;

public class HeartbeatReceiver extends Thread {

    private int port;
    ServerSocket serverSocket;
    InetAddress serverAddress;

    public HeartbeatReceiver(int _port){
        this.port = _port;
    }

    public void run() {
        final int serverPort = Node.getNodePort("L");

        try {
            serverAddress = InetAddress.getByName("localhost");
            serverSocket = new ServerSocket(serverPort, 50, serverAddress);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String message = input.readLine();
                String[] _messageArray = message.split(", ");
                String decision = _messageArray[1];
                UUID _uuid = UUID.fromString(_messageArray[0]);
                addToBuffer(_uuid, decision);

                input.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error creating multicast socket or joining group: " + e.getMessage());
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public synchronized void addToBuffer(UUID _uuid, String _messageArray) {
        Node.getJsonObjectMap().putIfAbsent(_messageArray, _uuid);
    }
}