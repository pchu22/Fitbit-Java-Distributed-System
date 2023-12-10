package Multicast;

import Records.FitbitRecord;
import Node.Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MulticastSender extends Thread {
    private final InetAddress group;
    private final int port;
    private final String role;
    private final ConcurrentHashMap<UUID, String> waitingQueue = new ConcurrentHashMap<>();
    private final FitbitRecord fitbitRecord;

    public MulticastSender(InetAddress group, int port, String role, FitbitRecord fitbitRecord) {
        this.group = group;
        this.port = port;
        this.role = role;
        this.fitbitRecord = fitbitRecord;
    }

    @Override
    public void run() {
        while (true) {
            try (MulticastSocket socket = new MulticastSocket()) {
                if (role.equals("L")) {
                    sendElementMessage(socket);
                    if (Node.getJsonObjectMap().isEmpty()) {
                        sendLeaderMessage(socket);
                    }
                } else {
                    Node currentNode = Node.getNeighbours().get(0);
                    if (Node.getNeighbours().size() < 4 && !currentNode.isRequestingInclusion()) {
                        currentNode.requestInclusion();
                        sendInclusionRequest(socket, currentNode);
                    } else {
                        sendHeartbeatMessage(socket);
                    }
                }

                Thread.sleep(20000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendElementMessage(MulticastSocket socket) throws IOException {
        String message = createElementMessage();
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
        socket.send(packet);
    }

    private String createElementMessage() throws RemoteException {
        StringBuilder messageBuilder = new StringBuilder();
        if (!fitbitRecord.getRecords().isEmpty()) {
            for (double[] record : fitbitRecord.getRecords()) {
                UUID uuid = UUID.randomUUID();
                String uuidToString = uuid.toString();
                waitingQueue.put(uuid, Arrays.toString(record));
                messageBuilder.append(uuidToString).append("//").append(Arrays.toString(record)).append(";");
            }
        }
        fitbitRecord.getRecords().clear();
        return messageBuilder.toString();
    }

    private void sendLeaderMessage(MulticastSocket socket) throws IOException {
        String message = createLeaderMessage();
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
        socket.send(packet);
    }

    private String createLeaderMessage() {
        int counter = 0;
        UUID uuid = null;
        StringBuilder messageBuilder = new StringBuilder();

        for (Map.Entry<String, UUID> entry : Node.getJsonObjectMap().entrySet()) {
            String key = entry.getKey();
            UUID uuidSecondHash = entry.getValue();
            for (Map.Entry<UUID, String> firstEntry : waitingQueue.entrySet()) {
                UUID uuidFirstHash = firstEntry.getKey();
                uuid = uuidFirstHash;
                if (uuidSecondHash.equals(uuidFirstHash)) {
                    String decision = key.split(",")[0];
                    System.out.println("Decision: " + decision);
                }
            }
        }
        if (counter == 2) {
            messageBuilder.append("IGNORE");
        } else {
            messageBuilder.append("ADD");
        }

        waitingQueue.clear();
        Node.getJsonObjectMap().clear();
        return messageBuilder.toString();
    }

    private void sendHeartbeatMessage(MulticastSocket socket) throws IOException {
        byte[] heartbeat = "heartbeat".getBytes();
        DatagramPacket heartbeatPacket = new DatagramPacket(heartbeat, heartbeat.length, group, port);
        socket.send(heartbeatPacket);
    }

    private void sendInclusionRequest(MulticastSocket socket, Node currentNode) throws IOException {
        String message = "inclusion-request," + currentNode.getPort();
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
        socket.send(packet);
    }
}
