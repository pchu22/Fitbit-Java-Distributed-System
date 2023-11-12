import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        try {
            Leader leader = new Leader("224.0.0.1", 4446, 8888);
            leader.start();
            System.out.println("Leader is online!");
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