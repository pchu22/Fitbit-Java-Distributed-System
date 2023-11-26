import java.util.ArrayList;
import java.util.List;

public class Main {

    private static Node leaderNode = new Node("L", 4446, Node.DistanceMetric.HAMMING);

    public static void main(String[] args) {
        List<Node> nodes = initializeNodes();
        for (Node node : nodes) {
            new Thread(node).start();
        }

        receiveNearestNeighbors();
    }

    private static List<Node> initializeNodes() {
        List<Node> nodes = new ArrayList<>();

        nodes.add(new Node("E", 4447, Node.DistanceMetric.MANHATTAN));
        nodes.add(new Node("E", 4448, Node.DistanceMetric.MINKOWSKI));
        nodes.add(new Node("E", 4449, Node.DistanceMetric.HAMMING));

        return nodes;
    }

    private static void receiveNearestNeighbors() {
        while (true) {
            List<Record> nearestNeighbors = MulticastReceiver.receiveNearestNeighbors(leaderNode);
            if (nearestNeighbors != null) {
                System.out.println("Nearest neighbors: " + nearestNeighbors);
            }
        }
    }

}
