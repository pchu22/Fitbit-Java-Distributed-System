import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;

public class HeartbeatSender extends Thread {

    private InetAddress address;
    private int port;

    public HeartbeatSender(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public void sendHeartbeat(List<String> pedidosPendentes) throws IOException {
        String message = "Heartbeat";
        pedidosPendentes.add(message);

        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address, port);
        socket.send(packet);
        socket.close();

        socket = new DatagramSocket();
        packet = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName("224.0.0.1"), 4446);
        socket.send(packet);
        socket.close();
    }
}
