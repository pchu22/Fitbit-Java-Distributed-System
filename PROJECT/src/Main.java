import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create the leader node
        Node leaderNode = new Node(true, new Hamming());
        leaderNode.initializeDataset();

        // Create three element nodes
        Node elementNode1 = new Node(false, new Hamming());
        Node elementNode2 = new Node(false, new Manhattan());
        Node elementNode3 = new Node(false, new Minkowski(2.0));

        // Initialize the dataset for element nodes
        elementNode1.initializeDataset();
        elementNode2.initializeDataset();
        elementNode3.initializeDataset();

        // Set the leader node as the first neighbor for element nodes
        elementNode1.getNeighbors().add(leaderNode);
        elementNode2.getNeighbors().add(leaderNode);
        elementNode3.getNeighbors().add(leaderNode);

        // Start the nodes
        leaderNode.startNode();
        elementNode1.startNode();
        elementNode2.startNode();
        elementNode3.startNode();
    }
}