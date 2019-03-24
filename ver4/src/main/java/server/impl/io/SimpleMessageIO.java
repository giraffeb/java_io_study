package server.impl.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.interfaces.BasicIO;
import server.interfaces.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * TODO: 클래스 이름이 사용목적에 적절하지 않습니다.
 */
public class SimpleMessageIO implements BasicIO {

    static Logger LOG = LoggerFactory.getLogger(SimpleMessageIO.class);


    @Override
    public byte[] readMessage(SocketChannel socketChannel, ByteBuffer byteBuffer) {
        byte[] message = null;

        int readCount = 0;
        int targetReadCount = Message.HEADER_SIZE; //초기에 읽은 사이즈는 헤더사이즈 만큼입니다.

        int bodySize = 0;


        try {
            readCount = socketChannel.read(byteBuffer);

            if(readCount < 0){
                //socketChannel closed
                socketChannel.close();
            }else if(readCount == 0) {
                //읽을게 없으면
                return null;
            }else{
                if(targetReadCount <= readCount){//헤더사이즈만큼 읽어진 경우.
                    byteBuffer.flip();
                    bodySize = byteBuffer.getInt();//Header가 단순히 인트사이즈만 다루므

                    targetReadCount = bodySize;
                    readCount -= Message.HEADER_SIZE;

                    if(targetReadCount <= readCount){ //바디크기만큼 읽어진 경우
                        message = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.position() + bodySize);
                        byteBuffer.get(byteBuffer.array(), byteBuffer.position(), bodySize); //바이트 버퍼에 읽은 부분 표시 -> position 값 증가
                    }else{
                        //
                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            try {
                socketChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        byteBuffer.compact();

        return message;
    }

    @Override
    public void sendMessage(SocketChannel socketChannel, ByteBuffer byteBuffer, byte[] message) {
        LOG.debug(byteBuffer.toString());

        byteBuffer.putInt(message.length);
        byteBuffer.put(message);
        byteBuffer.flip();

        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byteBuffer.compact();

    }
}
