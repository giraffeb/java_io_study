package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

public class GenericServerStarter implements ServerStarterInterface {

    private ServerSocket serverSocket;
    private int serverPort = 4444;
    public List<ChatUser> userList;
    public static Logger logger = Logger.getLogger(GenericServerStarter.class.getName());
    public ExecutorService threadPool;
    public ThreadPoolExecutor threadPoolExecutor;

    public GenericServerStarter(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * 서버소켓을 생성합니다.
     */
    @Override
    public void init() {
        try {
            logger.info(Thread.currentThread()+" : INIT()");
            this.serverSocket = new ServerSocket(this.serverPort);
            this.userList = Collections.synchronizedList(new ArrayList<>());
            this.threadPool = Executors.newFixedThreadPool(2);
            this.threadPoolExecutor = (ThreadPoolExecutor)this.threadPool;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 사용자 접속을 받아서, 사용자 소켓 및 ChatUser객체를 생성하는 메소드입니다.
     * 메인쓰레드에서 호출합니다.
     */
    @Override
    public void start() {
        logger.info(Thread.currentThread()+" : START()");

        Socket userSocket = null;
        ChatUser newUser = null;

        while(true){

            try{
                //block Method
                userSocket = this.serverSocket.accept();
                newUser = new ChatUser(userSocket, this);

                if(this.threadPoolExecutor.getActiveCount() >= this.threadPoolExecutor.getCorePoolSize()){
                        newUser.sender("SERVER IS FULL", newUser);
                        newUser.destroy();
                        continue;
                }

                this.userList.add(newUser);
                newUser.doReceive();
                logger.info(Thread.currentThread()+" : GET ChatUser ");
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * 서버 종료시 호출됩니다.
     */
    @Override
    public void shutdown() {

    }


    /**
     * 사용자로부터 받음 메시지를
     * 모든 사용자들에게 전달합니다.
     * @param msg
     * @param sender
     */
    public void echo(String msg, ChatUser sender){

        for(ChatUser receiver : this.userList){
            receiver.sender(msg, sender);
        }
    }
}
