

package Multicast;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;

public class MulticastReceiver extends Thread {
    private final InetAddress group;
    private final int port;
    private final BlockingQueue<String> messageQueue;

    public MulticastReceiver(String _group, int _port, BlockingQueue<String> _messageQueue) {
        try {
            this.group = InetAddress.getByName(_group);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.port = _port;
        this.messageQueue = _messageQueue;

    }

    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(4446)) {
            socket.joinGroup(group);
            while (true){
                byte[] buffer = new byte[1024];

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);;
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                if (!received.isEmpty()){
                    this.messageQueue.put(received);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}