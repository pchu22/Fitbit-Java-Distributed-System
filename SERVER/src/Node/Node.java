package Node;

import Unicast.HeartbeatSender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Node {
    private final int port;
    private static String role;
    private final int method;
    private static final ArrayList<Node> neighbours = new ArrayList<>();
    private static final ConcurrentHashMap<String, UUID> jsonObjectMap = new ConcurrentHashMap<>();
    private boolean requestInclusion = false;
    private static double[][] dataset;
    private static final LeaderNode leader = new LeaderNode();


    private static double[][] initializeDataset() {
        return new double[][]{
                {14, 150, 32, 18},
                {19, 8, 15, 7},
                {20, 178, 20, 20},
                {22, 210, 5, 45},
                {30, 175, 70, 80},
                {18, 122, 14, 19},
                {12, 88, 45, 32},
                {26, 199, 80, 62},
                {22, 150, 23, 15},
                {11, 60, 18, 28},
                {15, 120, 60, 40},
                {28, 160, 25, 55},
                {21, 180, 30, 35},
                {17, 205, 10, 50},
                {25, 130, 40, 30},
                {19, 95, 55, 48},
                {14, 220, 65, 70},
                {24, 170, 28, 42},
                {16, 110, 50, 60},
                {10, 130, 20, 25},
                {30, 190, 80, 75},
                {26, 155, 40, 35},
                {18, 145, 15, 20},
                {13, 205, 60, 50},
                {22, 165, 45, 65}
        };
    }

    public Node(int port, String role, int method) {
        this.port = port;
        this.role = role;
        this.method = method;
        this.dataset = initializeDataset();
    }

    public int getPort() {
        return port;
    }

    public int getMethod() {
        return method;
    }

    public String getRole() {
        return role;
    }

    public static ArrayList<Node> getNeighbours() {
        return new ArrayList<>(neighbours); // Returns a copy to prevent direct modification
    }

    public static ConcurrentHashMap<String, UUID> getJsonObjectMap() {
        return jsonObjectMap;
    }

    public static int getNodePort(String role) {
        for (Node node : neighbours) {
            if (node.getRole().equals(role)) {
                return node.getPort();
            }
        }
        return 0;
    }

    public static double[][] getDataset() {
        return dataset;
    }

    public boolean isRequestingInclusion() {
        return requestInclusion;
    }

    public void requestInclusion() {
        requestInclusion = true;
    }

    public void clearRequestInclusion() {
        requestInclusion = false;
    }

    @Override
    public String toString() {
        return "Node{" +
                "port=" + port +
                ", role='" + role + '\'' +
                ", method=" + method +
                '}';
    }

    public static void processMessage(String message) throws IOException {
        if (message.startsWith("inclusion-request")) {
            handleInclusionRequest(message);
        } else if (message.equals("heartbeat")) {
            HeartbeatSender.main(new String[]{message});
            leader.receivedHeartbeat(getNodePort(role)); // Adicionado
        } else if (message.equals("add")) {
            sendInclusionResponse(true, "E", 5002);
            communicateVotingResult("E", 5002, "ADD");
            addToDataset("E", 5002, "ADD");
        } else if (message.equals("ignore")) {
            sendInclusionResponse(false, "E", 5003);
            communicateVotingResult("E", 5003, "IGNORE");
            addToDataset("E", 5003, "IGNORE");
        } else if (!role.equals("L")) {
            String[] _message = message.split(";");
            // Dummy action
            System.out.println("Receiving another type of message!");
        }
        leader.checkAndRemoveInactiveNodes();
    }


    private static void handleInclusionRequest(String message) {
        String[] parts = message.split(",");
        String messageType = parts[0];
        int requestingPort = Integer.parseInt(parts[1]);
        String requesterRole = parts[2].trim();
        int requesterPort = Integer.parseInt(parts[3].trim());

        if (leader.consensusToRemoveNode(requesterPort)) { // Alterado
            sendInclusionResponse(true, requesterRole, requesterPort);
            communicateVotingResult(requesterRole, requesterPort, "ADD");
            addToDataset(requesterRole, requesterPort, "ADD");
        } else {
            sendInclusionResponse(false, requesterRole, requesterPort);
        }
    }

    private static boolean consensusReached() {
        int votesNeeded = Node.getNeighbours().size() / 2 + 1;
        int approvalCount = 0;
        for (Node neighbour : Node.getNeighbours()) {
            if (neighbour.isRequestingInclusion()) {
                approvalCount++;
            }
        }
        return approvalCount >= votesNeeded;
    }

    private static void sendInclusionResponse(boolean consensus, String requesterRole, int requesterPort) {
        try (MulticastSocket responseSocket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName("230.0.0.1");
            int port = 4446;

            String responseMessage = "inclusion-response," + consensus + "," + requesterRole + "," + requesterPort;
            byte[] responseBuffer = responseMessage.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, group, port);
            responseSocket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void communicateVotingResult(String requesterRole, int requesterPort, String message) {
        try (MulticastSocket resultSocket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName("230.0.0.1");
            int port = 4446;

            String resultMessage = "voting-result," + requesterRole + "," + requesterPort + "," + message;
            byte[] buffer = resultMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
            resultSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addToDataset(String requesterRole, int requesterPort, String message) {
        double[] recordToAdd = {1, 2, 3, 4};

        if ("ADD".equals(message)) {
            synchronized (dataset) {
                double[][] newDataset = new double[dataset.length + 1][recordToAdd.length];
                System.arraycopy(dataset, 0, newDataset, 0, dataset.length);
                newDataset[dataset.length] = recordToAdd;
                dataset = newDataset;
            }
        }
    }
}
