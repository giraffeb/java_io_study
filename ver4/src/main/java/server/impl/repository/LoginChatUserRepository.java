package server.impl.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.impl.vo.chat.ChatUser;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 현재 로그인한 사용자와, 소켓채널을 매핑합니다.
 * 아이디 값을 어떻게 처리할까 세션이라고 생각하면 될텐데
 *
 */
public class LoginChatUserRepository {

    static Logger LOG = LoggerFactory.getLogger(LoginChatUserRepository.class);
    Map<SocketChannel, ChatUser> loginChatUserList;



    public void init(){
        this.loginChatUserList = new LinkedHashMap<>();
    }

    public LoginChatUserRepository() {
        init();
    }


    public void printUserList(){
        LOG.debug("#START :: PRINT_USER_LIST : "+this.loginChatUserList.size());
        for(SocketChannel socketChannel : this.loginChatUserList.keySet()){
            LOG.debug(socketChannel + this.loginChatUserList.get(socketChannel).toString());

        }
        LOG.debug("#END :: PRINT_USER_LIST");
    }

    public void saveLoginChatUser(SocketChannel socketChannel, ChatUser loginedChatUser){
        ChatUser tempChatUser= findByChatUserSocketChannel(socketChannel);
        //사용자가 이미 로그인한 경우.
        if(tempChatUser != null && tempChatUser.getId().equals(loginedChatUser.getId())){
            return;
        }


        this.loginChatUserList.put(socketChannel, loginedChatUser);

    }

    public ChatUser findByChatUserSocketChannel(SocketChannel socketChannel){
        ChatUser foundedChatUser = null;

        foundedChatUser = this.loginChatUserList.get(socketChannel);

        if(foundedChatUser == null){
            LOG.debug("해당 사용자가 로그인한 상태가 아닙니다.");
        }

        return foundedChatUser;
    }

    public ChatUser findByChatUserId(String id){
        List<ChatUser> chatUserList = new ArrayList<>(this.loginChatUserList.values());
        ChatUser foundedChatUser = null;


        for(ChatUser curChatUser : chatUserList){
            if(curChatUser.getId().equals(id)){
                foundedChatUser = curChatUser;
                break;
            }
        }

        if(foundedChatUser == null){
            LOG.debug("해당 사용자가 로그인한 상태가 아닙니다.");
        }

        return foundedChatUser;
    }

    public boolean isLogin(String id){
        ChatUser foundedChatUser = findByChatUserId(id);
        boolean flag = false;

        if(foundedChatUser != null){
            flag = true;
        }

        return flag;
    }



}
