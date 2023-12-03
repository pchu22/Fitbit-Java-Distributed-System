package Node;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Node {
    private final int port;
    private final String role;
    private final int method;
    private static final ArrayList<Node> neighbours = new ArrayList<>();
    private static final ConcurrentHashMap<String, UUID> jsonObjectMap = new ConcurrentHashMap<>();
    private boolean requestInclusion = false;

    public Node(int port, String role, int method) {
        this.port = port;
        this.role = role;
        this.method = method;
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
}
