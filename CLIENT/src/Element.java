import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Element {
    private DatagramSocket unicastSocket;
    private int unicastPort;
    private InetAddress leaderAddress;
    private String elementName;

    public Element(String leaderAddress, int unicastPort, String elementName) throws IOException {
        try {
            this.unicastSocket = new DatagramSocket();
            this.unicastPort = unicastPort;
            this.leaderAddress = InetAddress.getByName(leaderAddress);
            this.elementName = elementName;
        } catch (IOException e) {
            e.printStackTrace();
            throw e; // Re-throw the exception after printing the stack trace
        }
    }

    public void sendHeartbeat(int targetPort) throws IOException {
        String heartbeatMessage = String.format(HEARTBEAT_MESSAGE_TEMPLATE, elementName);
        byte[] buffer = heartbeatMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, leaderAddress, targetPort);
        unicastSocket.send(packet);

        // Receive response from leader
        byte[] responseData = new byte[256];
        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
        unicastSocket.receive(responsePacket);
        String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
        System.out.println("Response from leader: " + response);
    }

    public void close() {
        try {
            if (unicastSocket != null && !unicastSocket.isClosed()) {
                unicastSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUnicastPort() { return unicastPort; }

    public String getElementName() { return elementName; }

    private static final String HEARTBEAT_MESSAGE_TEMPLATE = "Heartbeat from %s";
}
