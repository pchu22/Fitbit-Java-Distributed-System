package Unicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

public class UnicastReceiver extends Thread{
    private DatagramSocket unicastSocket;
    private final BlockingQueue<String> messageQueue;
    public UnicastReceiver(DatagramSocket _unicastSocket, BlockingQueue<String>_messageQueue){
        this.unicastSocket = _unicastSocket;
        this.messageQueue = _messageQueue;
    }

    @Override
    public void run(){
        while (true) {
            try {
                byte[] messageBuffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(messageBuffer, messageBuffer.length);
                unicastSocket.receive(packet);

                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                this.messageQueue.put(receivedMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
