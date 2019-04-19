package client;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.impl.io.SimpleMessageFactory;
import server.impl.vo.chat.ChatUser;
import server.impl.vo.message.MessageState;
import server.impl.vo.message.RequestMessage;
import server.impl.vo.message.ResponseMessage;
import server.interfaces.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientMessgePingPongTest {

    static Logger LOG = LoggerFactory.getLogger(ClientCommandTest.class);



    @Test
    public void 로그인메시지_테스트() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 4444));
        socketChannel.configureBlocking(true);
        ByteBuffer byteBuffer = ByteBuffer.allocate(Message.MESSAGE_BUFFER_SIZE);

        RequestMessage requestMessage = new RequestMessage().setState(MessageState.LOGIN).setChatUser(new ChatUser().setId("Hello").setPw("new World"));

        LOG.debug(requestMessage.toString());
        SimpleMessageFactory simpleMessageFactory = new SimpleMessageFactory();

        simpleMessageFactory.sendRequestMessage(socketChannel, byteBuffer, requestMessage);


        ResponseMessage responseMessage = simpleMessageFactory.readResponseMessage(socketChannel, byteBuffer);
        LOG.debug(responseMessage.toString());

        socketChannel.close();
    }


    @Test
    public void 메시지_전달_테스트() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 4444));
        socketChannel.configureBlocking(true);
        ByteBuffer byteBuffer = ByteBuffer.allocate(Message.MESSAGE_BUFFER_SIZE);

        RequestMessage requestMessage = new RequestMessage()
                                            .setState(MessageState.MESSAGE)
                                            .setMessage("Hello new world this is message");


        LOG.debug(requestMessage.toString());
        SimpleMessageFactory simpleMessageFactory = new SimpleMessageFactory();

        simpleMessageFactory.sendRequestMessage(socketChannel, byteBuffer, requestMessage);


        ResponseMessage responseMessage = simpleMessageFactory.readResponseMessage(socketChannel, byteBuffer);
        LOG.debug(responseMessage.toString());

        socketChannel.close();
    }

    @Test
    public void 채팅방_목록요청하기() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 4444));
        socketChannel.configureBlocking(true);
        ByteBuffer byteBuffer = ByteBuffer.allocate(Message.MESSAGE_BUFFER_SIZE);

        RequestMessage requestMessage = new RequestMessage()
                .setState(MessageState.CHAT_ROOM_LIST);



        LOG.debug(requestMessage.toString());
        SimpleMessageFactory simpleMessageFactory = new SimpleMessageFactory();

        simpleMessageFactory.sendRequestMessage(socketChannel, byteBuffer, requestMessage);


        ResponseMessage responseMessage = simpleMessageFactory.readResponseMessage(socketChannel, byteBuffer);
        LOG.debug(responseMessage.toString());

        socketChannel.close();

    }


    @Test
    public void 채팅방_입장하기() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 4444));
        socketChannel.configureBlocking(true);
        ByteBuffer byteBuffer = ByteBuffer.allocate(Message.MESSAGE_BUFFER_SIZE);

        RequestMessage requestMessage = new RequestMessage()
                .setState(MessageState.CHAT_ROOM_JOIN)
                ;



        LOG.debug(requestMessage.toString());
        SimpleMessageFactory simpleMessageFactory = new SimpleMessageFactory();

        simpleMessageFactory.sendRequestMessage(socketChannel, byteBuffer, requestMessage);


        ResponseMessage responseMessage = simpleMessageFactory.readResponseMessage(socketChannel, byteBuffer);
        LOG.debug(responseMessage.toString());

        socketChannel.close();
    }


    @Test
    public void 채팅방_생성하기() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 4444));
        socketChannel.configureBlocking(true);
        ByteBuffer byteBuffer = ByteBuffer.allocate(Message.MESSAGE_BUFFER_SIZE);

        RequestMessage requestMessage = new RequestMessage()
                .setState(MessageState.CHAT_ROOM_CREATE);



        LOG.debug(requestMessage.toString());
        SimpleMessageFactory simpleMessageFactory = new SimpleMessageFactory();

        simpleMessageFactory.sendRequestMessage(socketChannel, byteBuffer, requestMessage);


        ResponseMessage responseMessage = simpleMessageFactory.readResponseMessage(socketChannel, byteBuffer);
        LOG.debug(responseMessage.toString());

        socketChannel.close();
    }
}
