package Multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashSet;
import java.util.Set;

import Node.Node;

public class MulticastReceiver extends Thread {

    private final String role;
    private final MulticastSocket socket;
    private final InetAddress group;
    private final int port;
    private final Set<String> receivedMessages = new HashSet<>();
    private final int method;
    private final double[][] dataset;

    public MulticastReceiver(InetAddress group, int port, String role, int method) throws IOException {
        this.group = group;
        this.port = port;
        this.role = role;
        this.method = method;
        this.socket = new MulticastSocket(port);
        this.socket.joinGroup(group);
        this.dataset = initializeDataset();
    }

    private double[][] initializeDataset() {
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

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData()).trim();
                if (!receivedMessages.contains(message)) {
                    receivedMessages.add(message);
                    processMessage(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.leaveGroup(group);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processMessage(String message) {
        if (message.startsWith("inclusion-request")) {
            handleInclusionRequest(message);
        } else if (!message.equals("heartbeat") && !message.equals("add") && !message.equals("not add") && !role.equals("L") && message.length() != 0) {
            String[] _message = message.split(";");
        }
    }

    private void handleInclusionRequest(String message) {
        String[] parts = message.split(",");
        String messageType = parts[0];
        int requestingNodePort = Integer.parseInt(parts[1]);

        if (consensusReached()) {
            sendInclusionResponse(true, requestingNodePort);
        } else {
            sendInclusionResponse(false, requestingNodePort);
        }
    }

// ...

    private boolean consensusReached() {
        int votesNeeded = Node.getNeighbours().size() / 2 + 1;
        int approvalCount = 0;

        for (Node neighbour : Node.getNeighbours()) {
            if (neighbour.isRequestingInclusion()) {
                approvalCount++;
            }
        }

        return approvalCount >= votesNeeded;
    }

    private void sendInclusionResponse(boolean consensus, int requestingNodePort) {
        try (MulticastSocket responseSocket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName("230.0.0.1");
            int port = 4446;

            String responseMessage = "inclusion-response," + consensus;
            byte[] responseBuffer = responseMessage.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, group, port);

            responseSocket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
