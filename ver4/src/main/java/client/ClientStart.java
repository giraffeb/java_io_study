package client;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientStart {

    public static void main(String[] args) throws IOException {

        //쓰레드 컨트롤용
        ExecutorService executorService = new ThreadPoolExecutor(1,1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        //사용자의 입력을 받음.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        //서버와 연결.
        SocketChannel client = SocketChannel.open(new InetSocketAddress("127.0.0.1", 4444));
        client.configureBlocking(false);
        ByteBuffer buf = ByteBuffer.allocate(1024);
        ByteBuffer sendMessageBuffer = ByteBuffer.allocate(1024);

//        File log = new File("log.txt");

        Runnable messageReader = () ->{
            int count = 0;
            String message = null;
            while(true){

                try {
                    count = client.read(buf);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(count < 0){
                    System.out.println("Connection refused.");
                    System.exit(0); // status mean?

                }
                else if(count == 0){
                    continue;
                }

                buf.flip();
                message = new String(buf.array(), buf.position(), buf.limit());
                System.out.println("Server : "+message);
            }

        };

        executorService.execute(messageReader);
        String sendMessage = null;
        while(true){
            System.out.println("Enter Message (blocked) : ");
            sendMessage = br.readLine();

            sendMessageBuffer.put(sendMessage.getBytes());
            sendMessageBuffer.flip();

            client.write(sendMessageBuffer);
            sendMessageBuffer.clear();

        }

    }
}
