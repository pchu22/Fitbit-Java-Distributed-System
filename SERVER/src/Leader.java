import java.io.IOException;
import java.net.*;

public class Leader extends Thread {
    private MulticastSocket multicastSocket;
    private DatagramSocket unicastSocket;
    private InetAddress group;
    private byte[] buffer;
    private int multicastPort;
    private int unicastPort;

    private volatile boolean running = true;

    public Leader(String multicastAddress, int multicastPort, int unicastPort) throws IOException {
        this.multicastPort = multicastPort;
        this.unicastPort = unicastPort;
        multicastSocket = new MulticastSocket(multicastPort);
        unicastSocket = new DatagramSocket(unicastPort);
        group = InetAddress.getByName(multicastAddress);
        buffer = new byte[256];
    }

    public void run() {
        try {
            multicastSocket.joinGroup(group);

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                unicastSocket.receive(packet);

                // Check if the packet is from a multicast address
                if (packet.getAddress().isMulticastAddress()) {
                    // Handling of a multicast packet
                    String multicastMessage = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Multicast message received: " + multicastMessage);
                } else {
                    // When a heartbeat is received from any element, a response is sent via unicast
                    String elementAddress = packet.getAddress().getHostAddress();
                    int elementUnicastPort = packet.getPort();

                    // Extract element name from the heartbeat message
                    String heartbeatMessage = new String(packet.getData(), 0, packet.getLength());
                    String elementName = extractElementName(heartbeatMessage);

                    // Send response via unicast
                    String responseMessage = String.format(HEARTBEAT_RESPONSE_TEMPLATE, elementName, elementAddress);
                    byte[] responseBuffer = responseMessage.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, InetAddress.getByName(elementAddress), elementUnicastPort);
                    unicastSocket.send(responsePacket);

                    System.out.println("Heartbeat received from " + elementName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (multicastSocket != null && !multicastSocket.isClosed()) {
                    multicastSocket.leaveGroup(group);
                    multicastSocket.close();
                }
                if (unicastSocket != null && !unicastSocket.isClosed()) {
                    unicastSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String extractElementName(String heartbeatMessage) {
        return heartbeatMessage.split(" ")[2];
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public int getUnicastPort() {
        return unicastPort;
    }

    public void stopServer() {
        running = false;
    }

    private static final String HEARTBEAT_RESPONSE_TEMPLATE = "Heartbeat received from %s at %s";
}