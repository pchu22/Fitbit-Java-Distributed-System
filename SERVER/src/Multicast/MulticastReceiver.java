package Multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashSet;
import java.util.Set;

import Node.Node;
import Unicast.HeartbeatSender;

public class MulticastReceiver extends Thread {

    private final String role;
    private final MulticastSocket socket;
    private final InetAddress group;
    private final int port;
    private final Set<String> receivedMessages = new HashSet<>();
    private final int method;

    public MulticastReceiver(InetAddress group, int port, String role, int method) throws IOException {
        this.group = group;
        this.port = port;
        this.role = role;
        this.method = method;
        this.socket = new MulticastSocket(port);
        this.socket.joinGroup(group);
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData()).trim();
                if (!receivedMessages.contains(message)) {
                    receivedMessages.add(message);
                    Node.processMessage(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.leaveGroup(group);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
