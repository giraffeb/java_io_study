package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;


/**
 * 서버에 접속하는 사용자 소켓과
 * 메시지 수신, 전송에 필요한 정보와 메소드를 가지고 있습니다.
 */
public class ChatUser {

    public String userName;
    public Socket userSocket;
    public GenericServerStarter server;

    public BufferedReader br;
    public BufferedWriter wr;

    public static Logger logger = Logger.getLogger(ChatUser.class.getName());

    /**
     * 사용자로를 생성합니다.
     * 소켓을 받아서 유틸리티 IO객체들을 생성합니다.
     *
     * @param userSocket
     * @param server
     */
    public ChatUser(Socket userSocket, GenericServerStarter server) {
        this.server = server;
        this.userSocket = userSocket;

        //소켓을 전달받아 유틸리티 리더들을 생성함
        try {
            br = new BufferedReader(new InputStreamReader(this.userSocket.getInputStream()));
            wr = new BufferedWriter(new OutputStreamWriter(this.userSocket.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
            try {
                userSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 사용자로부터 메시지를 수신하는 메소드인 receiver()를 사용해서,
     * 새로운 쓰레드를 생성합니다.
     */
    public void doReceive(){
        logger.info("ChatUser doReceive() call");
        try{
            this.server.threadPool.execute(()->{
               this.receiver();
            });

        }catch (Exception e){
            e.printStackTrace();
            this.destroy();
        }
    }

    /**
     * 사용자로부터 메시지를 수신하는 메소드입니다.
     */
    public void receiver(){
        String msg = null;

        while(true){
            try{
                msg  = br.readLine();
                if(msg.length() <= 0 || msg == null){
                    continue;
                }
                //최초에 사용자 이름을 받음.
                if(this.userName == null){
                    this.userName = msg;
                    continue;
                }
                logger.info(this.messageFomatter(msg, this));
                //서버로 메시지 전달.
                this.server.echo(msg, this);

            }catch(SocketException e){
                this.destroy();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                this.destroy();
            }
        }
    }

    /**
     * 사용자에게 전달하는 메시지 양식을 결정합니다.
     *
     * @param msg
     * @param sender
     * @return
     */
    public String messageFomatter(String msg, ChatUser sender){

        String formattedMessage = null;
        if(sender == this){
            formattedMessage = new StringBuffer("SENDER : ME"+", MESSAGE : " + msg).toString();
        }
        formattedMessage = new StringBuffer("SENDER : "+sender.userName +", MESSAGE : " + msg).toString();

        return formattedMessage;
    }

    /**
     * 메시지를 사용자에게 전송합니다.
     *
     * @param msg
     * @param sender
     */
    public void sender(String msg, ChatUser sender){
        try{
            wr.write(this.messageFomatter(msg,sender));
            wr.newLine();
            wr.flush();
        }catch(Exception e){

        }

    }

    /**
     * 메시지 수신에서 예외가 발생하면, 이 객체와 소켓을 정리합니다.ㅣ
     */
    public void destroy(){
        try {
            this.userSocket.close();
            this.server.userList.remove(this);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
