package client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientStarter {
    public static void main(String[] args) {
        String serverAddr = "localhost";
        int serverPort = 4444;

        Socket socket = null;

        BufferedReader br  = null;
        BufferedWriter wr = null;


        try {
            socket = new Socket(serverAddr, serverPort);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        }catch (Exception e){
            e.printStackTrace();
        }

        //수신부
        //Exception 부분 정리해야함.
        // 계층적 구조나 세련된 처리방법을
        BufferedReader finalBr = br;
        new Thread(()->{
           String msg = null;

           while(true){
               try {
                   msg = finalBr.readLine();
                   if(msg.length() <= 0){
                       continue;
                   }
                   System.out.println(msg);

               } catch (SocketException se){
                   se.printStackTrace();
                   System.out.println("EXIT");
                   System.exit(0);
               } catch (IOException e) {
                   e.printStackTrace();
               } catch (NullPointerException ne){
                   System.out.println("EXIT");
                   System.exit(0);
               }
           }
        }).start();

        //
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        String sendMSg = null;

        while(true){
            try {
                Thread.sleep(300);
                System.out.print("TYPE : ");
                sendMSg = inputReader.readLine();

                wr.write(sendMSg);
                wr.newLine();
                wr.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}
