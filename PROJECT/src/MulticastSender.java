import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSender {
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;

    public void multicast(String multicastMessage) throws IOException {
        socket = new DatagramSocket();
        group = InetAddress.getByName("230.0.0.0");
        buf = multicastMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
        socket.send(packet);
        socket.close();
    }

    public static void sendRecord(Record record, Node node) {
        String message = "RECORD:" + record.getValue();
        byte[] data = message.getBytes();
        int dataLength = data.length;
        int chunkSize = 512;

        for (int i = 0; i < dataLength; i += chunkSize) {
            int chunkLength = Math.min(chunkSize, dataLength - i);

            if (dataLength < 0) {
                System.err.println("Error: Invalid data length");
                return;
            }

            if (chunkLength < 0) {
                System.err.println("Error: Invalid chunk length");
                return;
            }

            if (i < 0) {
                System.err.println("Error: Invalid offset");
                return;
            }

            byte[] chunkData = new byte[chunkLength];

            if (i + chunkLength > dataLength) {
                chunkLength = dataLength - i;
            }

            System.arraycopy(data, i, chunkData, 0, chunkLength);

            DatagramPacket packet = new DatagramPacket(chunkData, chunkLength, node.getPort());

            try {
                MulticastSocket socket = new MulticastSocket(4446);
                socket.send(packet);
            } catch (IOException e) {
                System.err.println("Error sending record chunk: " + e.getMessage());
            }
        }
    }
}