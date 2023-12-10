package Unicast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

public class HeartbeatSender extends Thread {

    private InetAddress address;
    private MulticastSocket socket;

    public HeartbeatSender(InetAddress _address) throws IOException {
        this.address = _address;
        this.socket = new MulticastSocket(4446);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java HeartbeatSender <ID> <decision>");
            return;
        }

        String ID = args[0];
        String decision = args[1];

        try {
            Socket socket = new Socket("localhost", 3030);

            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println(ID + ", " + decision);
            System.out.println("The message has been sent to the server: " + ID);

            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
