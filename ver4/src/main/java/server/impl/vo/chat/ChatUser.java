package server.impl.vo.chat;

import java.io.Serializable;

/**
 * 사용자 정보를 기록할 객체.
 */
public class ChatUser implements Serializable {

    int uid;

    String id;
    String pw;

    public int getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }


    public ChatUser setUid(int uid) {
        this.uid = uid;
        return this;
    }

    public ChatUser setId(String id) {
        this.id = id;
        return this;
    }

    public ChatUser setPw(String pw) {
        this.pw = pw;
        return this;
    }

    @Override
    public String toString() {
        return "ChatUser{" +
                "uid=" + uid +
                ", id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                '}';
    }
}
