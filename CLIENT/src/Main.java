import java.io.IOException;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        try {
            int numberOfElements = 3;
            int leaderUnicastPort = 8888;

            Element[] elements = new Element[numberOfElements];

            try {
                // Create multiple elements with unique IP addresses
                for (int i = 0; i < numberOfElements; i++) {
                    String elementAddress = InetAddress.getLocalHost().getHostAddress();
                    elements[i] = new Element(elementAddress, leaderUnicastPort, "element" + i);
                }

                // Continuous loop to send heartbeats from each element to the leader
                while (true) {
                    for (Element element : elements) {
                        element.sendHeartbeat(leaderUnicastPort);
                    }
                    Thread.sleep(20000); // Wait for 20 seconds before sending the next set of heartbeats
                }
            } finally {
                // Close resources when they are no longer needed
                for (Element element : elements) {
                    element.close();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
