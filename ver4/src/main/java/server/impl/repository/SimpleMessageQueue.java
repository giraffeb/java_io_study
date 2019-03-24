package server.impl.repository;

import server.interfaces.Message;
import server.interfaces.MessageQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 비지니스 처리를 하기 위해 대기하는 메시지를 관리하는 객체.
 */
public class SimpleMessageQueue implements MessageQueue {

    List<Message> messageList;

    private void init(){
        this.messageList = Collections.synchronizedList(new ArrayList<Message>());
    }

    public SimpleMessageQueue() {
        init();
    }

    @Override
    public boolean isEmpty() {
        return this.messageList.isEmpty();
    }

    @Override
    public Message popMessage() {
        if(this.isEmpty()){
            return null;
        }

        return this.messageList.get(0);
    }

    @Override
    public void pushMessage(Message newMessage) {
        this.messageList.add(newMessage);
    }
}
