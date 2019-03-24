package server.impl.vo.message;

import server.impl.vo.chat.ChatUser;
import server.interfaces.Message;

import java.util.List;
import java.util.Map;


/**
 * 클라이언트의 요청에 대한
 * 서버의 응답을 담는 객체입니다.
 */
public class ResponseMessage implements Message{

    MessageState messageState;
    boolean correct;

    List<Map<String, String>> chatRoomNameList;

    int chatRoomId;
    String chatRoomTitle;

    String message;

    ChatUser receiveChatUser;
    ChatUser messageSendChatUser;


    public MessageState getMessageState() {
        return messageState;
    }

    public List<Map<String, String>> getChatRoomNameList() {
        return chatRoomNameList;
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    public String getChatRoomTitle() {
        return chatRoomTitle;
    }

    public String getMessage() {
        return message;
    }

    public boolean isCorrect() {
        return correct;
    }

    public ChatUser getReceiveChatUser() {
        return receiveChatUser;
    }

    public ChatUser getMessageSendChatUser() {
        return messageSendChatUser;
    }

    public ResponseMessage setCorrect(boolean correct) {
        this.correct = correct;
        return this;
    }

    public ResponseMessage setMessageState(MessageState messageState) {
        this.messageState = messageState;
        return this;
    }

    public ResponseMessage setChatRoomNameList(List<Map<String, String>> chatRoomNameList) {
        this.chatRoomNameList = chatRoomNameList;
        return this;
    }

    public ResponseMessage setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
        return this;
    }

    public ResponseMessage setChatRoomTitle(String chatRoomTitle) {
        this.chatRoomTitle = chatRoomTitle;
        return this;
    }

    public ResponseMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseMessage setReceiveChatUser(ChatUser receiveChatUser) {
        this.receiveChatUser = receiveChatUser;
        return this;
    }

    public ResponseMessage setMessageSendChatUser(ChatUser messageSendChatUser) {
        this.messageSendChatUser = messageSendChatUser;
        return this;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "messageState=" + messageState +
                ", correct=" + correct +
                ", chatRoomNameList=" + chatRoomNameList +
                ", chatRoomId=" + chatRoomId +
                ", chatRoomTitle='" + chatRoomTitle + '\'' +
                ", message='" + message + '\'' +
                ", receiveChatUser=" + receiveChatUser +
                '}';
    }
}
