import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
    private Node node;
    private DatasetManager datasetManager;

    public MulticastReceiver(Node node) {
        this.node = node;
        this.datasetManager = new DatasetManager();
    }
    public static List<Record> receiveNearestNeighbors(Node node) {
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        try {
            MulticastSocket socket = new MulticastSocket(4446);
            socket.receive(packet);

            String message = new String(packet.getData(), 0, packet.getLength());
            if (message.startsWith("NEAREST_NEIGHBORS")) {
                String[] data = message.split(":");
                List<Record> nearestNeighbors = new ArrayList<>();

                for (int i = 1; i < data.length; i++) {
                    int value = Integer.parseInt(data[i]);
                    Record nearestNeighbor = new Record(value);
                    nearestNeighbors.add(nearestNeighbor);
                }

                return nearestNeighbors;
            }
        } catch (IOException e) {
            System.err.println("Error receiving nearest neighbors: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                MulticastSocket socket = new MulticastSocket(4446);
                InetAddress group = InetAddress.getByName("224.0.0.1");
                socket.joinGroup(group);

                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                socket.receive(packet);
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());

                if (receivedMessage.startsWith("PUT")) {
                    JSONObject request = new JSONObject(receivedMessage);
                    String key = request.getString("key");
                    String value = request.getString("value");
                    JSONObject keyObject = new JSONObject(key);
                    JSONObject valueObject = new JSONObject(value);


                    DatasetManager datasetManager = new DatasetManager();
                    boolean exists = datasetManager.isSimilar(keyObject, valueObject);

                    if (!exists) {
                        LeaderElection leaderElection = new LeaderElection();
                        Node leader = leaderElection.electLeader(LeaderElection.nodes);
                        leader.sendMessage("PUT:key:value");
                    }
                }
            } catch (IOException e) {
                System.err.println("Error receiving datagram: " + e.getMessage());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}