package server.impl.vo.chat;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChatRoom {

    int chatRoomId;
    String chatRoomTitle;

    Map<SocketChannel, ChatUser> chatUserList;

    public void init(){

        this.chatUserList = new LinkedHashMap<>();
    }

    public ChatRoom() {
        init();
    }

    public boolean isEmpty(){
        return this.chatUserList.isEmpty();
    }

    public List<SocketChannel> getUserSocketChannelList(){
        if(isEmpty()){
            return null;
        }

        return new ArrayList<>(this.chatUserList.keySet());
    }

    public ChatRoom addChatUser(ChatUser newUser, SocketChannel socketChannel){
        this.chatUserList.put(socketChannel, newUser );
        return this;
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    public String getChatRoomTitle() {
        return chatRoomTitle;
    }


    public ChatRoom setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
        return this;
    }

    public ChatRoom setChatRoomTitle(String chatRoomTitle) {
        this.chatRoomTitle = chatRoomTitle;
        return this;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "chatRoomId=" + chatRoomId +
                ", chatRoomTitle='" + chatRoomTitle + '\'' +
                ", chatUserList=" + chatUserList +
                '}';
    }
}
