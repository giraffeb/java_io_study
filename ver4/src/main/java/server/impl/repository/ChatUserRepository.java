package server.impl.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.impl.vo.chat.ChatUser;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 회원 가입되어 있는 유저를 저장 및 관리하는 역할을 합니다.
 * 프로그램이 내려가있는 경우를 대비해서 파일로 저장하는 기능이 필요합니다.
 */

//TODO: 유저 파일로 저장하는 기능 만들기.
public class ChatUserRepository {

    static Logger LOG = LoggerFactory.getLogger(ChatUserRepository.class);
    Map<Integer, ChatUser> chatuserList;

    File db;

    public void init(){

        this.chatuserList = new LinkedHashMap<>();
        this.db = new File("chatUser.db");
        this.readFile();
    }

    public ChatUserRepository() {
        init();
    }

    public boolean isEmpty(){
        return this.chatuserList.isEmpty();
    }

    public ChatUser findById(String id){
        if(isEmpty()){
            return null;
        }

        ChatUser foundedChatUser = null;
        List<ChatUser> userList = new ArrayList<>(this.chatuserList.values());

        for(ChatUser cur : userList){
            if(cur.getId().equals(id)){
                foundedChatUser = cur;
                break;
            }
        }

        if(foundedChatUser == null){
            LOG.debug("아이디가 일치하는 사용자를 찾을 수 없습니다.");
        }

        return foundedChatUser;
    }

    public ChatUser findByUID(int uid){
        if(isEmpty()){
            return null;
        }

        ChatUser foundedChatUser = null;

        foundedChatUser = this.chatuserList.get(uid);


        return foundedChatUser;
    }

    public void saveChatUser(ChatUser newChatUser){

        this.chatuserList.put(newChatUser.hashCode(), newChatUser);

        this.saveFile();
    }

    public void deleteChatUserById(String id){
        if(isEmpty()){
            return;
        }

        ChatUser targetChatUser = null;
        int targetUid = -1;

        targetChatUser = findById(id);

        if(targetChatUser == null){
            return;
        }
        targetUid = targetChatUser.getUid();

        this.chatuserList.remove(targetUid);
    }

    public void deleteChatUser(ChatUser chatUser){
        if(isEmpty()){
            return;
        }
        int targetUid = chatUser.getUid();
        this.chatuserList.remove(targetUid);
    }

    public void readFile(){
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(this.db));
            this.chatuserList = (Map<Integer, ChatUser>)objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void saveFile(){
        ObjectOutputStream objectOutputStream;

        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(this.db));
            objectOutputStream.writeObject(this.chatuserList);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
