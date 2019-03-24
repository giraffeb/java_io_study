package server.impl.repository;

import server.impl.vo.chat.ChatRoom;
import sun.misc.SoftCache;

import java.util.*;

public class ChatRoomRepository {

    Map<Integer, ChatRoom> chatRoomList;

    public void init(){
        this.chatRoomList = new LinkedHashMap<>();
    }

    public ChatRoomRepository() {
        init();
    }

    public ChatRoom findByChatRoomID(int chatRoomId){
        ChatRoom curChatRoom = null;
        curChatRoom = this.chatRoomList.get(chatRoomId);

        return curChatRoom;
    }

    public int saveChatRoom(ChatRoom newChatRoom){
        int chatRoomID = newChatRoom.hashCode();
        newChatRoom.setChatRoomId(chatRoomID);
        this.chatRoomList.put(chatRoomID, newChatRoom);

        return chatRoomID;
    }

    public List<ChatRoom> getChatRoomList(){

        return new ArrayList<>(this.chatRoomList.values());
    }


}
