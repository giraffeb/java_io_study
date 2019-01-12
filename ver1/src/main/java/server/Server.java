package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* TCP 프로토콜을 사용해서
* 채팅 서버를 만듭니다.
*
* 1) 전체가 하나의 방 개념으로 이루어진 채팅 서버를 만듭니다.
* 2) 각각의 사용자가 접속시 입, 출력 쓰레드를 각각 1개씩 만듭니다.
* 3) 사용자가 보낸 메시지는 다른 모든 사용자들에게 동일하게 발송됩니다.
*
* 기본 개념을 이렇게
*
* */
public class Server {
    public int serverPort;
    public ServerSocket server;


//    public List<Object[]> msgQueue;
    public List<User> userList;

    //자바 기본 로깅 라이브러리 사용
    private final static Logger logger = Logger.getLogger(Server.class.getName());


    //서버의 소켓을 생성하는 과정을 해보자.
    public Server(int serverPort) throws Exception{

        logger.log(Level.INFO, "Server Initiate :: Handlers :: " + logger.getHandlers());

        this.serverPort = serverPort;
        this.server = new ServerSocket(this.serverPort);

        this.userList = Collections.synchronizedList(new ArrayList<>());


    }

    //서버가 사용자 연결을 받아서, 소켓 및 사용자 객체만들기
    public void doProcess() throws Exception{
        logger.log(Level.INFO, "ServerStart");
        Socket newUserSocket = null;
        User newUser = null;

        while(true){
            //유저 접속시까지 블로킹됨 - 그리고 동기임
            newUserSocket = this.server.accept();

            //새로운 소켓을 이용해서 유저객체를 만들고, 각각 입,출력 쓰레드 생성.
            newUser = new User(newUserSocket, this, userList);
            this.userList.add(newUser);

            newUser.doReceive();
            newUser.wr.write("WELCOME TO SERVER");
            newUser.wr.newLine();
            newUser.wr.flush();


        }

    }

    public void sendMessageToAllUsers(User sender, String msg) throws IOException {
        logger.log(Level.INFO,
                Thread.currentThread()
                        +" :: FROM "+ sender.username
                        +" :: SEND MESSAGE :: " + msg);

        for(User u : this.userList){
            u.sendMessage(u, msg);
        }

    }

}
