package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.impl.io.SimpleMessageFactory;
import server.impl.vo.chat.ChatRoom;
import server.impl.vo.chat.ChatUser;
import server.impl.vo.message.MessageState;
import server.impl.vo.message.RequestMessage;
import server.impl.vo.message.ResponseMessage;
import server.interfaces.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

public class ClientStarter {

    static Logger LOG = LoggerFactory.getLogger(ClientStart.class);


    SocketChannel socketChannel;
    ByteBuffer byteBuffer;
    SimpleMessageFactory simpleMessageFactory;
    Scanner scanner;

    ChatUser me;
    ChatRoom currentChatRoom;

    public void init(){

        try {
            this.socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 4444));
            this.socketChannel.configureBlocking(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.byteBuffer = ByteBuffer.allocate(Message.MESSAGE_BUFFER_SIZE);
        this.simpleMessageFactory = new SimpleMessageFactory();


        this.scanner = new Scanner(System.in);
    }

    public void start(){

        if( printLoginCommand() == true){
            printCommand();
        }
    }

    public boolean printLoginCommand(){
        System.out.println("LOGIN or CREATE USER");
        System.out.println("0. LOGIN");
        System.out.println("1. CREATE USER");

        System.out.print("input : ");

        int cmd = this.scanner.nextInt();
        this.scanner.nextLine();

        boolean flag = false;

        switch (cmd){
            case 0:
                flag = doLogin();
                break;
            case 1:
                flag = createChatUser();
                if(flag == true){
                    flag = doLogin();
                }
                break;
        }

        return flag;
    }


    public void printCommand(){
       while(true){
           System.out.println("Choose Command : ");
           MessageState[] commandArr = { MessageState.CHAT_ROOM_LIST, MessageState.CHAT_ROOM_CREATE };

           for(int i=0;i<commandArr.length;i++){
               System.out.println(i+" : "+commandArr[i]);
           }

           int cmd  = this.scanner.nextInt();
           this.scanner.nextLine();//nextInt()이후의 /r/n문자 제거.

           MessageState choosedCmd = commandArr[cmd];

           System.out.println("->"+cmd);

           switch (choosedCmd){
               case CHAT_ROOM_LIST:
                   boolean result = doChatRoomList();
                   if(result == true){
                       doMessage();
                   }
                   break;
               case CHAT_ROOM_CREATE:
                   createChatRoom();
                   doMessage();
                   break;
           }
       }
    }


    public boolean doLogin() {
        LOG.debug("doLogin");
        int failureCount = 0;
        boolean result = false;

        while (true) {

            if (failureCount > 2) {
                System.exit(1);
            }

            System.out.println("LOGIN INPUT ID/PW");
            System.out.print("ID : ");
            String id = this.scanner.nextLine();
            System.out.print("PW : ");
            String pw = this.scanner.nextLine();

            this.me = new ChatUser().setId(id).setPw(pw);

            RequestMessage requestMessage = new RequestMessage()
                    .setState(MessageState.LOGIN)
                    .setChatUser(this.me);

            this.simpleMessageFactory.sendRequestMessage(this.socketChannel, this.byteBuffer, requestMessage);

            ResponseMessage responseMessage = this.simpleMessageFactory.readResponseMessage(this.socketChannel, this.byteBuffer);

            if (responseMessage.getMessageState() == MessageState.LOGIN
                    && responseMessage.isCorrect() == true) {
                result = true;

                me = new ChatUser().setId(id).setPw(pw);

                break;

            }else {
                failureCount++;
                continue;
            }

        }
        return result;
    }


    public boolean doChatRoomList(){
        LOG.debug("doChatRoomList");

        RequestMessage requestMessage = new RequestMessage()
                .setState(MessageState.CHAT_ROOM_LIST);

        this.simpleMessageFactory.sendRequestMessage(this.socketChannel, this.byteBuffer, requestMessage);

        ResponseMessage responseMessage = this.simpleMessageFactory.readResponseMessage(this.socketChannel, this.byteBuffer);

        System.out.println("CHOOSE CHATROOM NUMBER");
        for(int i=0;i < responseMessage.getChatRoomNameList().size(); i++){
            Map<String, String> tempChatRoomData = responseMessage.getChatRoomNameList().get(i);
            System.out.println(i+" : "+tempChatRoomData.get("chatRoomTitle"));
        }

        System.out.print("input : ");
        int choosedRoom = this.scanner.nextInt();
        int chatRoomId = Integer.parseInt(responseMessage.getChatRoomNameList().get(choosedRoom).get("chatRoomId"));

        LOG.debug(responseMessage.toString());

        return doJoinChatRoom(chatRoomId);
    }


    public boolean doJoinChatRoom(int chatRoomId){

        RequestMessage requestMessage = new RequestMessage()
                .setState(MessageState.CHAT_ROOM_JOIN)
                .setChatRoomId(chatRoomId)
                .setChatUser(this.me);

        this.simpleMessageFactory.sendRequestMessage(this.socketChannel, this.byteBuffer, requestMessage);

        ResponseMessage responseMessage = this.simpleMessageFactory.readResponseMessage(this.socketChannel, this.byteBuffer);
        LOG.debug(responseMessage.toString());
        if(responseMessage.getMessageState() == MessageState.CHAT_ROOM_JOIN &&
                responseMessage.isCorrect() == true){
            this.currentChatRoom = new ChatRoom().setChatRoomId(responseMessage.getChatRoomId());

            return true;
        }

        return false;
    }


    public void createChatRoom(){
        LOG.debug("createChatRoom");

        System.out.print("Input ChatRoom Title : ");

        String chatRoomTitle = this.scanner.nextLine();

        RequestMessage requestMessage = new RequestMessage()
                                            .setState(MessageState.CHAT_ROOM_CREATE)
                                            .setChatRoomTitle(chatRoomTitle)
                                            .setChatUser(this.me);

        this.simpleMessageFactory.sendRequestMessage(this.socketChannel, this.byteBuffer, requestMessage);

        LOG.debug(requestMessage.toString());

        ResponseMessage responseMessage = this.simpleMessageFactory.readResponseMessage(this.socketChannel, this.byteBuffer);

        this.currentChatRoom = new ChatRoom().setChatRoomId(responseMessage.getChatRoomId())
                                                .setChatRoomTitle(responseMessage.getChatRoomTitle());

        LOG.debug(responseMessage.toString());

    }

    public boolean createChatUser(){
        LOG.debug("createChatUser");

        System.out.println("CREATE USER INPUT ID, PW");

        System.out.print("ID : ");
        String id = this.scanner.nextLine();

        System.out.print("PW : ");
        String pw = this.scanner.nextLine();

        ChatUser newChatUser = new ChatUser().setId(id)
                                            .setPw(pw);

        RequestMessage requestMessage = new RequestMessage()
                                            .setState(MessageState.CREATE_USER)
                                            .setChatUser(newChatUser);

        this.simpleMessageFactory.sendRequestMessage(this.socketChannel, this.byteBuffer, requestMessage);

        ResponseMessage responseMessage = this.simpleMessageFactory.readResponseMessage(this.socketChannel,this.byteBuffer);

        if(responseMessage.getMessageState() == MessageState.CREATE_USER &&
            responseMessage.isCorrect() == true){
            this.me = newChatUser;
            return true;
        }else{
            return false;
        }


    }


    public void doMessage(){
        LOG.debug("doMessage");
        try {
            this.socketChannel.configureBlocking(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestMessage requestMessage = null;
//        ResponseMessage responseMessage = null;
        String msg = null;


        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(()->{
            while(true){
                ResponseMessage responseMessage = this.simpleMessageFactory.readResponseMessage(this.socketChannel, ByteBuffer.allocate(Message.MESSAGE_BUFFER_SIZE));
                if(responseMessage != null)
                    if(responseMessage.getMessage() != null){
                        LOG.debug(responseMessage.toString());
                        System.out.println(responseMessage.getMessageSendChatUser().getId() + " :: " + responseMessage.getMessage());
                    }
                if(socketChannel.isConnected() == false){
                    System.exit(1);
//                    break;
                }
            }

        });

        while(true){
            msg = this.scanner.nextLine();
            if(msg.equals("exit")){
                try {
                    this.socketChannel.configureBlocking(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            requestMessage = new RequestMessage().setState(MessageState.MESSAGE)
                                                .setChatRoomId(this.currentChatRoom.getChatRoomId())
                                                .setMessage(msg)
                                                .setChatUser(this.me);

            LOG.debug(requestMessage.toString());
            this.simpleMessageFactory.sendRequestMessage(this.socketChannel, this.byteBuffer, requestMessage);

        }

    }

}
