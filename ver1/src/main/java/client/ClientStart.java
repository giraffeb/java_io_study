package client;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientStart {

    public static final Logger logger = Logger.getLogger(ClientStart.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {

        Socket socket = new Socket("127.0.0.1", 4444);



        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter Server Name : ");
        String servername = userInput.readLine();
        wr.write(servername);
        wr.newLine();
        wr.flush();


        //서버로부터 메시지를 수신하는 쓰레드
        new Thread(()->{
            String msg = null;

            while(true){
                try {
                    msg = br.readLine();
                    if(msg.length() <= 0 || msg == null){ continue; }

                    System.out.println("RECEIVE -> "+ msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();


        //서버로 메시지 보내기
        String newMsg = null;
        while (true){
            Thread.sleep(300);
            System.out.print("TYPE -> ");
            newMsg = userInput.readLine();

            wr.write(newMsg);
            wr.newLine();
            wr.flush();

        }

    }
}
