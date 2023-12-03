import Records.TCP;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        startServer("5001", "L", "0");
        startServer("5002", "E", "1");
        startServer("5003", "E", "2");
        startServer("5004", "E", "3");

        startTCP("8000");
    }

    private static void startServer(String port, String role, String method) throws IOException {
        String[] serverArgs = {port, role, method};
        Server.main(serverArgs);
    }

    private static void startTCP(String port) {
        String[] tcpArgs = {port};
        TCP.main(tcpArgs);
    }
}
