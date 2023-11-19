import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Queue;

public class HeartbeatSender extends Thread {
    private int unicastPort;
    private Node node;
    private DatagramSocket socket;
    private Queue<String> decisionQueue;

    public HeartbeatSender(Node node, int unicastPort, Queue<String>decisionQueue) {
        this.node = node;
        this.unicastPort = unicastPort;
        this.decisionQueue = decisionQueue;
        try {
            socket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                int heartbeatValue = Utils.randomHeartbeat();
                String heartbeatMessage = "Heartbeat|" + node.getNodeID() + "|" + heartbeatValue;
                DatagramPacket heartbeatPacket = new DatagramPacket(heartbeatMessage.getBytes(), heartbeatMessage.length(), InetAddress.getByName("224.0.0.1"), unicastPort);
                socket.send(heartbeatPacket);
                Thread.sleep(5000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
