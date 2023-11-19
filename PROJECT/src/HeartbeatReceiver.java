import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class HeartbeatReceiver extends Thread {
    private int unicastPort;
    private int leaderHeartbeatPort;
    private DatagramSocket socket;

    public HeartbeatReceiver(int unicastPort, int leaderHeartbeatPort) {
        this.unicastPort = unicastPort;
        this.leaderHeartbeatPort = leaderHeartbeatPort;
        try {
            socket = new DatagramSocket(unicastPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivedPacket);
                String receivedMessage = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                System.out.println("Received heartbeat: " + receivedMessage);
                buffer = new byte[1024];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
