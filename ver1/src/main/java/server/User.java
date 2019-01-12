package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    public Socket mySocket;
    public Server server;
    public List<User> userList;

    public BufferedReader br;
    public BufferedWriter wr;

    public String username = null;

    public static final Logger logger = Logger.getLogger(User.class.getName());


    public User(Socket mySocket, Server server, List<User> userList) throws Exception{
        this.mySocket = mySocket;
        this.server = server;
        this.userList = userList;

        br = new BufferedReader(new InputStreamReader(this.mySocket.getInputStream()));
        wr = new BufferedWriter(new OutputStreamWriter(this.mySocket.getOutputStream()));

    }

    public void doReceive(){
        try{
            new Thread(this::receiveMessage).start();
        }catch (NullPointerException e){
            e.printStackTrace();
            this.userList.remove(this);
        }
    }

    /**
     * 유저에게 받은 메시지를
     * 서버의 메시지 큐에 넣는다.
     */
    public void receiveMessage(){
        String msg = null;

        while(true){
            try {
                msg = br.readLine();
                if(msg == null){
                    this.mySocket.close();
                    this.userList.remove(this);
                    break;
                }
                if(msg.length() <= 0 ) { continue; }
                if(this.username == null){
                    this.username = msg;
                    continue;
                }

                logger.log(Level.INFO,
                        Thread.currentThread()
                                +":: RECEIVE MESSAGE :: " + msg);

                this.server.sendMessageToAllUsers(this, msg);

            }

            catch (IOException e) { e.printStackTrace(); }

        }
    }


    /**
     * 사용자가 서버로 메시지를 보내면
     * 서버가 이 메소드를 호출해서
     * 사용자에게 전달함.
     * @param sender
     * @param msg
     * @throws Exception
     */
    public void sendMessage(User sender, String msg) throws IOException{

        wr.write("FROM :: "+sender.username + " :: SEND MESSAGE :: " + msg);
        wr.newLine();
        wr.flush();
    }

}
