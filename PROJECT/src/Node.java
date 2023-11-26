import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;

public class Node implements Serializable, Runnable {
    private static HashMap<Integer, Node> nodeList = new HashMap<>();

    private int ID;
    private String role;
    private int port;
    private int k;
    private List<Record> records;
    private DistanceMetric metric;

    public enum DistanceMetric {
        HAMMING(1),
        MANHATTAN(2),
        MINKOWSKI(3);

        private final int value;

        DistanceMetric(int value) {
            this.value = value;
        }

        public static double hammingDistance (Record x, Record y) {
            String binStr1 = Integer.toBinaryString(x.getValue());
            String binStr2 = Integer.toBinaryString(y.getValue());
            int lenDiff = Math.abs(binStr1.length() - binStr2.length());
            if (binStr1.length() < binStr2.length()) {
                binStr1 = "0".repeat(lenDiff) + binStr1;
            } else {
                binStr2 = "0".repeat(lenDiff) + binStr2;
            }
            int count = 0;
            for (int i = 0; i < binStr1.length(); i++) {
                if (binStr1.charAt(i) != binStr2.charAt(i)) {
                    count++;
                }
            }
            return count;
        }

        static int manhattanDistanceSum(int arr[], int n) {
            Arrays.sort(arr);
            int res = 0, sum = 0;
            for (int i = 0; i < n; i++) {
                res += (arr[i] * i - sum);
                sum += arr[i];
            }
            return res;
        }

        static double manhattanTotalDistance(int[] x, int[] y, int n) { return manhattanDistanceSum(x, n) + manhattanDistanceSum(y, n); }

        public static double pRoot(double value, double root) {
            double rootValue = 1.0 / root;
            return new BigDecimal(value).pow((int) rootValue).setScale(3, RoundingMode.HALF_UP).doubleValue();
        }

        public static double minkowskiDistance(double[] x, double[] y, double pValue) {
            double sum = 0.0;
            for (int i = 0; i < x.length; i++) {
                double difference = x[i] - y[i];
                sum += Math.pow(Math.abs(difference), pValue);
            }
            return pRoot(sum, pValue);
        }

        public int getValue() {
            return value;
        }
    }

    public Node(String _role, int _port, DistanceMetric _metric) {
        this.role = _role;
        this.port = _port;
        this.metric = _metric;
        this.records = new ArrayList<>();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String _role) {
        this.role = _role;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(int _port) {
        this.port = _port;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(int _ID) {
        this.ID = _ID;
    }

    public void addRecord(Record record) {
        records.add(record);
    }

    public void sendHeartbeat() {
        try {
            MulticastSocket socket = new MulticastSocket(4446);
            InetAddress group = InetAddress.getByName("224.0.0.1");

            String heartbeatMessage = String.format("%s:%d", role, port);
            byte[] data = heartbeatMessage.getBytes();

            DatagramPacket packet = new DatagramPacket(data, data.length, group, 4446);
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Error sending heartbeat: " + e.getMessage());
        }
    }

    public List<Record> getNearestNeighbours(Record record) {
        List<Double> distances = new ArrayList<>();
        for (Record existingRecord : records) {
            switch (metric) {
                case HAMMING:
                    distances.add(DistanceMetric.hammingDistance(record, existingRecord));
                    break;
                case MANHATTAN:
                    distances.add(DistanceMetric.manhattanTotalDistance(new int[]{record.getValue()}, new int[]{existingRecord.getValue()}, 1));
                    break;
                case MINKOWSKI:
                    distances.add(DistanceMetric.minkowskiDistance(new double[]{record.getValue()}, new double[]{existingRecord.getValue()}, 2.0));
                    break;
            }
        }

        Collections.sort(distances);

        List<Record> nearestNeighbours = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            nearestNeighbours.add(records.get(distances.get(i).intValue()));
        }
        return nearestNeighbours;
    }

    private Record extractRecordFromPacket(DatagramPacket packet) {
        String payload = new String(packet.getData(), 0, packet.getLength());
        String[] recordData = payload.split(":");

        int value = Integer.parseInt(recordData[1]);

        return new Record(value); // Fixed constructor call
    }

    private void processHeartbeatMessage(String heartbeatMessage) {
        String[] heartbeatData = heartbeatMessage.split(":");

        String role = heartbeatData[0];
        int port = Integer.parseInt(heartbeatData[1]);

        if (nodeList.containsKey(port)) {
            Node existingNode = nodeList.get(port);
            existingNode.setRole(role);
        } else {
            Node newNode = new Node(role, port, metric);
            nodeList.put(port, newNode);
        }
    }

    public void sendMessage(String message) {
        try {
            MulticastSocket socket = new MulticastSocket(4446);
            InetAddress group = InetAddress.getByName("224.0.0.1");

            byte[] buf = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);

            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
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
                if (receivedMessage.startsWith("RECORD")) {

                    Record record = extractRecordFromPacket(packet);

                    addRecord(record);
                } else if (receivedMessage.startsWith("HEARTBEAT")) {
                    processHeartbeatMessage(receivedMessage);
                } else {
                    System.out.println("Received unknown message: " + receivedMessage);
                }
            } catch (IOException e) {
                System.err.println("Error receiving datagram: " + e.getMessage());
            }
        }
    }
}