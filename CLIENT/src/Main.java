import java.io.IOException;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        try {
            int numberOfElements = 3; // Number of elements you wish to create
            int leaderUnicastPort = 8888;

            Element[] elements = new Element[numberOfElements];

            // "for" cycle to create multiple elements with unique IP addresses
            // (in this case, all elements will have the same IP address since the host machine is the same)
            for (int i = 0; i < numberOfElements; i++) {
                String elementAddress = InetAddress.getLocalHost().getHostAddress();
                elements[i] = new Element(elementAddress, leaderUnicastPort, "element" + i);
            }

            // Continuous loop to send heartbeats from each element to the leader every 3 seconds
            while (true) {
                for (Element element : elements) {
                    element.sendHeartbeat(leaderUnicastPort);
                }
                Thread.sleep(20000);
            }

        } catch (NumberFormatException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}