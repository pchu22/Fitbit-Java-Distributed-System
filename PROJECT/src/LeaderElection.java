import java.util.List;

public class LeaderElection {
    public static List<Node> nodes;

    public static List<Node> getNodes() {
        return nodes;
    }

    public static void setNodes(List<Node> nodes) {
        LeaderElection.nodes = nodes;
    }

    public Node electLeader(List<Node> nodes) {
        Node leader = null;
        int minId = Integer.MAX_VALUE;

        for (Node node : LeaderElection.nodes) {
            int nodeId = node.getID();

            if (nodeId < minId) {
                minId = nodeId;
                leader = node;
            }
        }

        return leader;
    }
}
