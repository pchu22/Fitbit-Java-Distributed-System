import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

public class HeartbeatReceiver extends Thread {

    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
    private List<String> pendingRequests = new ArrayList<>();

    public void run() {
        try {
            socket = new MulticastSocket(4446);
            InetAddress group = InetAddress.getByName("224.0.0.1");
            socket.joinGroup(group);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    if ("Heartbeat".equals(received)) {
                        checkPendingRequests();
                    }
                } catch (IOException e) {
                    System.err.println("Error receiving datagram: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error creating multicast socket or joining group: " + e.getMessage());
        }
    }

    private void checkPendingRequests() {
        if (pendingRequests.isEmpty()) {
            return;
        }

        for (String request : pendingRequests) {
            if ("Heartbeat".equals(request)) {
                pendingRequests.remove(request);
            }
        }
    }
}