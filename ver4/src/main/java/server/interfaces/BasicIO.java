package server.interfaces;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface BasicIO {

    byte[] readMessage(SocketChannel socketChannel, ByteBuffer readBuffer);
    void sendMessage(SocketChannel socketChannel, ByteBuffer sendBuffer, byte[] message);

}
