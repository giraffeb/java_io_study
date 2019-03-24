package server.impl.vo.message;

import server.impl.vo.chat.ChatUser;
import server.interfaces.Message;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 클라이언트에서 서버로 요청을 보낼때 사용하는 객체입니다.
 * 요청에 필요한 정보들을 담고 있습니다.
 */
public class RequestMessage implements Message{

    SocketChannel senderSocketChannel;
    ByteBuffer byteBuffer;

    MessageState state;

    String chatRoomTitle;
    int ChatRoomId;

    ChatUser chatUser;


    String message;

    public MessageState getState() {
        return state;
    }

    public String getChatRoomTitle() {
        return chatRoomTitle;
    }

    public int getChatRoomId() {
        return ChatRoomId;
    }

    public String getMessage() {
        return message;
    }

    public SocketChannel getSenderSocketChannel() {
        return senderSocketChannel;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public ChatUser getChatUser() {
        return chatUser;
    }

    public RequestMessage setChatUser(ChatUser chatUser) {
        this.chatUser = chatUser;
        return this;
    }

    public RequestMessage setSenderSocketChannel(SocketChannel senderSocketChannel) {
        this.senderSocketChannel = senderSocketChannel;
        return this;
    }

    public RequestMessage setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
        return this;
    }

    public RequestMessage setState(MessageState state) {
        this.state = state;
        return this;
    }

    public RequestMessage setChatRoomTitle(String chatRoomTitle) {
        this.chatRoomTitle = chatRoomTitle;
        return this;
    }

    public RequestMessage setChatRoomId(int chatRoomId) {
        ChatRoomId = chatRoomId;
        return this;
    }

    public RequestMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "RequestMessage{" +
                "senderSocketChannel=" + senderSocketChannel +
                ", byteBuffer=" + byteBuffer +
                ", state=" + state +
                ", chatRoomTitle='" + chatRoomTitle + '\'' +
                ", ChatRoomId=" + ChatRoomId +
                ", receiveChatUser=" + chatUser +
                ", message='" + message + '\'' +
                '}';
    }
}
