import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        try {
            Leader leader = new Leader("224.0.0.1", 4446, 8888);
            leader.start();
            System.out.println("Leader is online!");

            // Wait for user input to stop the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Press Enter to stop the server.");
            reader.readLine();

            // Stop the server gracefully
            leader.stopServer();

            try {
                leader.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Leader is offline!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
