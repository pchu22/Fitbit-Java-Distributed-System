import Multicast.MulticastReceiver;
import Multicast.MulticastSender;
import Node.Node;
import Unicast.HeartbeatReceiver;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;

import static java.lang.Integer.parseInt;

public class Server {
    private final DatagramSocket socket;
    private final int port;
    private final String role;

    public Server(int port, String role) throws IOException {
        this.port = port;
        this.role = role;
        this.socket = new DatagramSocket(port);
    }

    public static void main(String[] args) throws IOException {
        int port = parseInt(args[0]);
        String role = args[1];
        int method = parseInt(args[2]);

        InetAddress group = InetAddress.getByName("localhost");
        int multicastPort = 4446;

        Node node = new Node(port, role, method);
        Node.getNeighbours().add(node);

        if (Node.getNeighbours().size() == 4) {
            startMulticastReceiversAndHeartbeatReceivers(group, multicastPort);

            startMulticastSenders(group, multicastPort);
        }
    }

    private static void startMulticastReceiversAndHeartbeatReceivers(InetAddress group, int multicastPort) throws IOException {
        for (Node neighbour : Node.getNeighbours()) {
            MulticastReceiver multicastReceiver = new MulticastReceiver(group, multicastPort, neighbour.getRole(), neighbour.getMethod());
            multicastReceiver.start();

            if (neighbour.getRole().equals("L")) {
                HeartbeatReceiver heartbeatReceiver = new HeartbeatReceiver(neighbour.getPort());
                heartbeatReceiver.start();
            }
        }
    }

    private static void startMulticastSenders(InetAddress group, int multicastPort) throws RemoteException {
        for (Node neighbour : Node.getNeighbours()) {
            MulticastSender multicastSender = new MulticastSender(group, multicastPort, neighbour.getRole(), new Records.FitbitRecord());
            multicastSender.start();
        }
    }
}
