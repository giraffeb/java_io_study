package client;


import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.impl.io.SimpleMessageFactory;
import server.impl.vo.message.MessageState;
import server.impl.vo.message.RequestMessage;
import server.impl.io.SimpleMessageIO;
import server.impl.vo.message.ResponseMessage;
import server.interfaces.Message;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


/**
 * 클라이언트에서 사용되는 명령어들을 테스트합니다.
 * 각 테스트마다 새로운 객체가 생성되므로 클래스 static변수로 필요한 반복되는 정보들을 유지합니다.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientCommandTest {

    static Logger LOG = LoggerFactory.getLogger(ClientCommandTest.class);

    @Test
    public void test1() throws IOException, InterruptedException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        SocketChannel client = SocketChannel.open(new InetSocketAddress("127.0.0.1", 4444));

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMessage("Hello");

        objectOutputStream.writeObject(requestMessage);

        LOG.debug("SIZE : "+byteOutputStream.toByteArray().length);
        SimpleMessageIO simpleMessageIO = new SimpleMessageIO();
        simpleMessageIO.sendMessage(client, byteBuffer, byteOutputStream.toByteArray());

        Thread.sleep(1000);
        client.close();
    }


    @Test
    public void objectRWTest() throws IOException, ClassNotFoundException {


        RequestMessage requestMessage =  new RequestMessage()
                                            .setMessage("Hello new world");

        LOG.debug("ORIGINAL");
        LOG.debug(requestMessage.toString());

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        objectOutputStream.writeObject(requestMessage);
        byte[] objecArray = byteOutputStream.toByteArray();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objecArray);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        RequestMessage temp = (RequestMessage) objectInputStream.readObject();

        LOG.debug("REWRITE");
        LOG.debug(temp.toString());
    }

}
