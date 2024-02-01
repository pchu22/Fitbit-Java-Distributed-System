package Multicast;

import Dataset.Dataset;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class MulticastSender extends Thread {
    private List<String> messages = new ArrayList<>();
    private InetAddress group;
    private int port;
    private int unicastPort;

    public MulticastSender(String _group, int _port, int _unicastPort) {
        try {
            this.group = InetAddress.getByName(_group);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.port = _port;
        this.unicastPort = _unicastPort;
    }

    public synchronized void addMessage(String command, String msg) {
        this.messages.add(command + ";" + msg);
        System.out.println("Leader port: " + port + " executed the following: " + command + ";" + msg);
    }

    private synchronized void sendMessage(MulticastSocket socket) throws IOException {
        if (this.messages.isEmpty()){
            String allElementsMessages = ("Leader;" + unicastPort + "&" + "alive;&");
            byte[] buffer = allElementsMessages.getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
            System.out.println("Leader port: " + port + " sent: [" + allElementsMessages + "]");
            socket.send(packet);
        } else{
            StringBuilder allElementsMessages = new StringBuilder("Leader;" + unicastPort + "&");

            for(String message : messages){
                allElementsMessages.append(message);
                allElementsMessages.append("&");
            }

            byte[] buffer = allElementsMessages.toString().getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
            System.out.println( "Leader port: " + port + " sent: [" + allElementsMessages + "]");
            socket.send(packet);
            this.messages.clear();
        }
    }

    @Override
    public void run() {
        while (true) {
            try (MulticastSocket socket = new MulticastSocket()) {
                addMessage("COMMIT", UUID.randomUUID() + ";" + new Dataset(70, 320,
                        6, 520, 130, 420, 2100, 90,
                        45, 7, 8500, 11000, 55,
                        11, 30, 110, 7500, 13000,
                        2500, 85, 22, 75, 300,
                        75).getString());
                sendMessage(socket);
                Thread.sleep(30000);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

