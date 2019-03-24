package server.impl.vo.message;


/**
 * 클라이언트에서 RequestMessage로 전달되는 요청들의 종류입니다.
 *
 */
public enum MessageState {

    LOGIN, MESSAGE, CHAT_ROOM_LIST, CHAT_ROOM_JOIN, CHAT_ROOM_CREATE, CREATE_USER

}
