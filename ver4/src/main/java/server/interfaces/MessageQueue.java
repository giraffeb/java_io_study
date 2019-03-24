package server.interfaces;


/**
 * SocketChannel을 통해서 사용자가 발송한 메시지를
 * 저장하는 MessageQueue를 구현하기 위한 인터페이스
 *
 */
public interface MessageQueue {

    boolean isEmpty();
    Message popMessage();
    void pushMessage(Message newMessage);
}
