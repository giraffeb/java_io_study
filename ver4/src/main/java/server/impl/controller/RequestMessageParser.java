package server.impl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.impl.io.SimpleMessageFactory;
import server.impl.repository.ChatRoomRepository;
import server.impl.repository.ChatUserRepository;
import server.impl.repository.LoginChatUserRepository;
import server.impl.vo.chat.ChatRoom;
import server.impl.vo.chat.ChatUser;
import server.impl.vo.message.MessageState;
import server.impl.vo.message.RequestMessage;
import server.impl.vo.message.ResponseMessage;
import server.interfaces.Message;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * 클라이언트로 들어온 RequestMessage를 해석해서
 * 적절한 메소드를 호출합니다.
 */
public class RequestMessageParser {

    static Logger LOG = LoggerFactory.getLogger(RequestMessage.class);
    /**
     * TODO: 의존성을 주입해야 할지, 자체적으로 생성할지 결정해야함.
     */
    ChatUserRepository chatUserRepository;
    LoginChatUserRepository loginChatUserRepository;
    ChatRoomRepository chatRoomRepository;

    SimpleMessageFactory simpleMessageFactory;


    /**
     * 임시적으로 만들어 놓은 부분입니다.
     */
    public void init(){
        this.chatUserRepository = new ChatUserRepository();

        this.loginChatUserRepository = new LoginChatUserRepository();

        this.chatRoomRepository = new ChatRoomRepository();
        this.chatRoomRepository.saveChatRoom(new ChatRoom().setChatRoomTitle("Hello new wolrd"));

        this.simpleMessageFactory = new SimpleMessageFactory();

    }

    public RequestMessageParser() {
        init();
    }

    /**
     * MessageState에 따라서 행동을 지정합니다.
     * @param requestMessage
     */
    public void parseMessage(RequestMessage requestMessage){

        switch (requestMessage.getState()){
            case CREATE_USER:
                LOG.debug("CREATE USER CASE");
                doCreateChatUser(requestMessage);
                break;

            case LOGIN:
                LOG.debug("parseMessage LOGIN CASE!");
                doLogin(requestMessage);
                break;

            case MESSAGE:
                LOG.debug("MESSAGE CASE");
                doMessage(requestMessage);
                break;

            case CHAT_ROOM_LIST:
                LOG.debug("CHAT ROOM LIST CASE");
                doChatRoomList(requestMessage);
                break;

            case CHAT_ROOM_JOIN:
                LOG.debug("CHAT ROOM JOIN CASE");
                doChatRoomJoin(requestMessage);
                break;

            case CHAT_ROOM_CREATE:
                LOG.debug("CHAT ROOM CREATION CASE");
                doChatRoomCreation(requestMessage);
                break;


        }
    }


    /**
     * 사용자가 등록되어 있으면, 아이디, 패스워드 일치여부 확인.
     * 없다면 없다고 알림.
     * @param requestMessage
     */
    public void doLogin(RequestMessage requestMessage){

        boolean flag = false;

        ChatUser targetUser = this.chatUserRepository.findById(requestMessage.getChatUser().getId());
        ResponseMessage responseMessage = null;
        //targetUser가 존재한다면 -> 로그인 처리하기
        //아니라면, 회원가입을 요청해야함.

        if(targetUser != null){
            //return success response message
            this.loginChatUserRepository.saveLoginChatUser(requestMessage.getSenderSocketChannel(), targetUser);

            responseMessage = new ResponseMessage()
                                    .setMessageState(MessageState.LOGIN)
                                    .setCorrect(true);

        }else{
            //return failure response message
            responseMessage = new ResponseMessage()
                                    .setMessageState(MessageState.LOGIN)
                                    .setCorrect(false);

        }

        this.simpleMessageFactory.sendResponseMessage(requestMessage.getSenderSocketChannel(), requestMessage.getByteBuffer(), responseMessage);

        LOG.debug(responseMessage.toString());
    }

    /**
     * requestMessage에서 가리키는 채팅방 ID에 소속된 모든 사용자들에게
     * 메시지를 에코합니다.
     * @param requestMessage
     */
    public void doMessage(RequestMessage requestMessage){
        int chatRoomId = requestMessage.getChatRoomId();
        ChatRoom curChatRoom = this.chatRoomRepository.findByChatRoomID(chatRoomId);

        //TODO: ResponseMessage 생성은 구조 개선 필요
        ResponseMessage responseMessage = new ResponseMessage()
                                                .setCorrect(true)
                                                .setChatRoomId(chatRoomId)
                                                .setMessageState(MessageState.MESSAGE)
                                                .setMessage(requestMessage.getMessage());

        ByteBuffer sendBuffer = ByteBuffer.allocate(Message.MESSAGE_BUFFER_SIZE);

        LOG.debug(responseMessage.toString());
        /**
         * TODO: 채팅방에 사람이 없으면 null point Exception이 발생합니다.
         * 최소한 1인 내가 있으므로 null은 안뜨게 되겠지.
         */

        ChatUser targetUser = null;
        for(SocketChannel cur : curChatRoom.getUserSocketChannelList()){

            if(cur == requestMessage.getSenderSocketChannel()){
                continue;
            }
            responseMessage.setMessageSendChatUser(requestMessage.getChatUser());
            this.simpleMessageFactory.sendResponseMessage(cur, sendBuffer, responseMessage);
        }

    }

    /**
     * 현재 서버에 열려있는 채팅방의 목록을 가져옴.
     */
    public void doChatRoomList(RequestMessage requestMessage){
        List<ChatRoom> chatRoomList =  this.chatRoomRepository.getChatRoomList();

        List<Map<String, String>> tempChatRoomList = new ArrayList<Map<String , String>>();

        for(ChatRoom curChatRoom : chatRoomList){

            Map<String, String> newData = new HashMap<>();
            newData.put("chatRoomId", String.valueOf(curChatRoom.getChatRoomId()) );
            newData.put("chatRoomTitle",curChatRoom.getChatRoomTitle());

            tempChatRoomList.add(newData);
        }

        ResponseMessage responseMessage = new ResponseMessage()
                                                .setMessageState(MessageState.CHAT_ROOM_LIST)
                                                .setCorrect(true)
                                                .setChatRoomNameList(tempChatRoomList);

        this.simpleMessageFactory.sendResponseMessage(requestMessage.getSenderSocketChannel(), requestMessage.getByteBuffer(), responseMessage);

    }

    /**
     * 채팅방에 들어가는 로직처리함.
     *
     */
    public void doChatRoomJoin(RequestMessage requestMessage){
        //사용자가 요청한 채팅방의 아이디값
        int chatRoomID = requestMessage.getChatRoomId();

        //조회
        ChatRoom curChatRoom = this.chatRoomRepository.findByChatRoomID(chatRoomID);

        //RequestMessage를 보낸 사용자의 아이디를 기반으로 검색
        //등록된 사용자인지 확인 및 정보 반환
        ChatUser curChatUser = this.chatUserRepository.findById(requestMessage.getChatUser().getId());

        //요청된 채팅방에 현재 사용자를 등록함.
        curChatRoom.addChatUser(curChatUser, requestMessage.getSenderSocketChannel());


        ResponseMessage responseMessage = null;

        //사용자가 요청한 채팅방이 존재한다면
        if(curChatRoom != null){
            /**
             * 해당 채팅방이 존재하는 경우
             */
            responseMessage = new ResponseMessage()
                                .setMessageState(MessageState.CHAT_ROOM_JOIN)
                                .setCorrect(true)
                                .setChatRoomId(curChatRoom.getChatRoomId())
                                .setChatRoomTitle(curChatRoom.getChatRoomTitle());

        }else{
            /**
             * 해당 채팅방이 존재하지 않는 경우
             */
            responseMessage = new ResponseMessage()
                                .setMessageState(MessageState.CHAT_ROOM_JOIN)
                                .setCorrect(false)
                                .setChatRoomId(chatRoomID);

        }

        //서버에서 로직 후 결과 반환
        this.simpleMessageFactory.sendResponseMessage(requestMessage.getSenderSocketChannel(), requestMessage.getByteBuffer(), responseMessage);

    }

    /**
     * 클라이언트에서 채팅방 생성을 요청한 경우
     * ChatRoom 객체를 생성하고, ChatRoomRepository에 등록함.
     * @param requestMessage
     */
    public void doChatRoomCreation(RequestMessage requestMessage){
        /**
         * 클라이언트에서 채팅방을 지정해서 보내는 것으로 함.
         */
        String chatRoomTitle = requestMessage.getChatRoomTitle();

        /**
         * 요청을 보낸 사용자가 등록된 사용자인지 한번더 확인 -> 일종의 권한 확인
         * TODO: 권한기능을 부여한다면 그쪽으로 확인하면 될 듯.
         */
        ChatUser thisChatUser = this.chatUserRepository.findById(requestMessage.getChatUser().getId());


        /**
         * 채팅방을 생성함.
         */
        ChatRoom newChatRoom = new ChatRoom()
                                    .setChatRoomTitle(chatRoomTitle)
                                    .addChatUser(thisChatUser, requestMessage.getSenderSocketChannel());

        /**
         * 채팅방 관리에 저장되면 그때 키값을 전달함.
         */
        int newChatRoomId = this.chatRoomRepository.saveChatRoom(newChatRoom);

        ResponseMessage responseMessage = new ResponseMessage()
                                                .setMessageState(MessageState.CHAT_ROOM_CREATE)
                                                .setCorrect(true)
                                                .setChatRoomTitle(newChatRoom.getChatRoomTitle())
                                                .setChatRoomId(newChatRoomId);

        /**
         * 응답을 보냄
         */
        this.simpleMessageFactory.sendResponseMessage(requestMessage.getSenderSocketChannel(), requestMessage.getByteBuffer(), responseMessage);


    }

    public void doCreateChatUser(RequestMessage requestMessage){

        ChatUser newChatUser = new ChatUser()
                                    .setId(requestMessage.getChatUser().getId())
                                    .setPw(requestMessage.getChatUser().getPw());

        this.chatUserRepository.saveChatUser(newChatUser);

        ResponseMessage responseMessage = new ResponseMessage()
                                            .setMessageState(MessageState.CREATE_USER)
                                            .setCorrect(true)
                                            .setReceiveChatUser(newChatUser);

        this.simpleMessageFactory.sendResponseMessage(requestMessage.getSenderSocketChannel(), requestMessage.getByteBuffer(), responseMessage);
    }


}
