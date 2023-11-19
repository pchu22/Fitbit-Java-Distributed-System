import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Node {
    private static int lastAssignedID = 0;
    private int nodeID;
    private boolean isLeader;
    private List<Node> neighbors = new ArrayList<>();
    private static final int multicastPort = 4446;
    private int unicastPort;
    private int leaderHeartbeatPort;
    private InetAddress multicastGroupAddress;
    private MulticastSocket multicastSocket;
    private HeartbeatSender heartbeatSender;
    private HeartbeatReceiver heartbeatReceiver;
    private String ipAddress;
    private List<Integer> dataset;
    private DistanceMetric distanceMetric;
    private Queue<String> decisionQueue = new LinkedBlockingQueue<>();
    private static final double THRESHOLD = 10.0;

    public Node(boolean isLeader, DistanceMetric distanceMetric) {
        this.nodeID = setNodeID();
        this.isLeader = isLeader;
        this.unicastPort = 8888 + nodeID;
        this.leaderHeartbeatPort = 9999;
        this.distanceMetric = distanceMetric;
        try {
            multicastGroupAddress = InetAddress.getByName("224.0.0.1");
            multicastSocket = new MulticastSocket(multicastPort);
            multicastSocket.joinGroup(multicastGroupAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startNode() {
        heartbeatSender = new HeartbeatSender(this, unicastPort, decisionQueue);
        heartbeatReceiver = new HeartbeatReceiver(unicastPort, leaderHeartbeatPort);

        new Thread(this::nodeDiscovery).start();
        heartbeatSender.start();
        heartbeatReceiver.start();
    }

    private void nodeDiscovery() {
        new Thread(this::sendDiscoveryMessage).start();
        receiveDiscoveryMessage();
    }

    private void sendDiscoveryMessage() {
        try {
            while (true) {
                String discoveryMessage = "Node " + nodeID + " is alive!";
                DatagramPacket discoveryPacket = new DatagramPacket(discoveryMessage.getBytes(), discoveryMessage.length(), multicastGroupAddress, multicastPort);
                multicastSocket.send(discoveryPacket);
                Thread.sleep(20000); // Waits 20 seconds before sending the next discovery message
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveDiscoveryMessage() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

            while (true) {
                multicastSocket.receive(receivedPacket);
                String receivedMessage = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                System.out.println("Received discovery message: " + receivedMessage);
                processDiscoveryMessage(receivedMessage, receivedPacket.getAddress());
                buffer = new byte[1024];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processDiscoveryMessage(String message, InetAddress senderAddress) {
        String ipAddress = senderAddress.getHostAddress();
        Node neighbor = neighbors.stream()
                .filter(node -> ipAddress.equals(node.getIpAddress()))
                .findFirst()
                .orElse(null);

        if (neighbor == null) {
            neighbor = new Node(false, distanceMetric); // Use the same distance metric as the current node
            neighbor.setIpAddress(ipAddress);
            neighbor.setNodeID();
            neighbors.add(neighbor);
            System.out.println("Added node " + neighbor.getNodeID() + " to the neighbors list.");

            if (dataset != null) {
                neighbor.setDataset(new ArrayList<>(dataset));
            }

            if (dataset != null && !dataset.isEmpty()) {
                int newRecord = Utils.randomHeartbeat();
                double similarity = calculateSimilarity(newRecord, neighbor.getDataset());
                System.out.println("Similarity with Node " + neighbor.getNodeID() + ": " + similarity);
            }
        }
    }

    private void sendDecisionToLeader(boolean addToDataset) {
        String decisionMessage = "Decision: " + (addToDataset ? "add" : "notadd");
        decisionQueue.add(decisionMessage);
    }

    public void receiveRequestAndDecide(int request) {
        if (dataset == null) {
            throw new IllegalStateException("Dataset not initialized. Set the dataset before making comparisons.");
        }

        double similarity = calculateSimilarity(request, dataset);
        System.out.println("Similarity with Request: " + similarity);

        boolean addToDataset = similarity > THRESHOLD;

        sendDecisionToLeader(addToDataset);
    }

    private double calculateSimilarity(int newRecord, List<Integer> records) {
        double sumSquaredDifferences = 0.0;
        for (int record : records) {
            double difference = distanceMetric.calculateDistance(newRecord, record);
            sumSquaredDifferences += Math.pow(difference, 2);
        }
        return Math.sqrt(sumSquaredDifferences);
    }

    public void initializeDataset() {
        if (dataset == null) {
            dataset = new ArrayList<>();
        }

        Thread heartbeatGenerator = new Thread(() -> {
            while (true) {
                int newHeartbeat = Utils.randomHeartbeat();

                // Null check added here
                boolean isSimilar = dataset != null && dataset.stream()
                        .anyMatch(existingHeartbeat -> calculateSimilarity(newHeartbeat, Collections.singletonList(existingHeartbeat)) > THRESHOLD);

                if (!isSimilar) {
                    dataset.add(newHeartbeat);
                    System.out.println("Added heartbeat to the dataset: " + newHeartbeat);
                } else {
                    System.out.println("Generated heartbeat is similar to existing ones. Not added.");
                }

                try {
                    // Sleep for 5 seconds
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        heartbeatGenerator.start();
    }


    private static synchronized int setNodeID() {
        return ++lastAssignedID;
    }

    public int getNodeID() {
        return nodeID;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public List<Node> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<Node> neighbors) {
        this.neighbors = neighbors;
    }

    public int getUnicastPort() {
        return unicastPort;
    }

    public InetAddress getMulticastGroupAddress() {
        return multicastGroupAddress;
    }

    public void setMulticastGroupAddress(InetAddress multicastGroupAddress) {
        this.multicastGroupAddress = multicastGroupAddress;
    }

    public MulticastSocket getMulticastSocket() {
        return multicastSocket;
    }

    public void setMulticastSocket(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public List<Integer> getDataset() {
        return dataset;
    }

    public void setDataset(List<Integer> dataset) {
        this.dataset = dataset;
    }
}
