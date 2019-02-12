package server.implement;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

public class GenericIOConnector {

    /**
     * 사용자들의 메시지 및 접속을 관리하는 객체를 만들고자 합니다.
     * 어차피 소켓채널은 유지하는 걸로하고 -> 소켓만큼 메모리 소모
     * 입력이 있을때만 쓰레드가 받아서 처리하기 -> 쓰레드 풀 사용
     * 입력받은 내용을 전파하는 역할은 다른 객체가 담당하자
     *
     * 연결하고 저장하는 곳으로 넘겨주고,
     *
     */

    Selector selector;
    ServerSocketChannel serverSocketChannel;
    List<SocketChannel> clientSocketList;
    int serverPort;

    public GenericIOConnector() {
    }

    public GenericIOConnector(int serverPort) {
        this.serverPort = serverPort;
    }

    //입출력에 필요한 처리를 합니다.
    public void init() throws IOException {
        System.out.println(this.getClass() + " :: init() ");

        this.selector = Selector.open();

        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.bind(new InetSocketAddress(this.serverPort));
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        this.clientSocketList =  Collections.synchronizedList(new ArrayList<>());

    }

    //클라이언트로 메시지를 보냅니다.

    /**
     * 메시지는 충분히 클 수 있다고 가정한다.
     * 버퍼보다 큰 사이즈의 데이터를 전달하고자 한다.
     * 버퍼는 차후 충분히 커질 수 있지만, 여기서는 버퍼를 매우 작게해서 버퍼보다 큰 파일을 전송하는 방법을 찾아보자.
     *
     * ver1 : 버퍼사이즈 만큼만 메시지를 전송하는 버전
     *
     * @param channel
     * @param message
     * @throws IOException
     */
    public void sendMessage(SocketChannel channel, String message) throws IOException{
        if(message.length() >= 1024){
            message = message.substring(0,1023);
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(message.getBytes());
        buffer.flip();

        channel.write(buffer);

    }


    //클라이언트에서 보낸 메세지를 표준출력합니다.
    public void printMessage(SocketChannel channel) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int count = channel.read(buffer);
        if(count < 0){
            System.out.println("Socket Channel Connection refused.");
            channel.close();
            return;
        }
        buffer.flip();

        String message = new String(buffer.array(), buffer.position(), buffer.limit());

        System.out.println(channel.getLocalAddress()+" : "+message);

    }


    //연결등 입출력을 처리합니다.
    public void start() throws IOException, NoSuchMethodException {
        Class[] cargs = new Class[1];
        cargs[0] = GenericIOConnector.class;
        System.out.println(this.getClass()+" :: "+this.getClass().getMethod("start", null));

        SocketChannel client = null;

        //입출력을 대기합니다.
        while(true){

            //입출력이 없으면 대기
            if(selector.selectNow() <= 0){
                continue;
            }

            Set<SelectionKey> SetKeys = selector.selectedKeys();
            Iterator<SelectionKey> keys = SetKeys.iterator();
            //입출력이 있을때,
            while(keys.hasNext()){
                SelectionKey key = keys.next();

                //입출력이 접속이라면
                if(key.isAcceptable()){
                    client = ((ServerSocketChannel)key.channel()).accept();
                    System.out.println("CONNECT client : "+client.getLocalAddress());
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    sendMessage(client, "Hello");

                }
                //입력이 들어온다면
                else if(key.isReadable()){
                    client = (SocketChannel) key.channel();
                    System.out.println("Client : "+client.getRemoteAddress()+" Request");
                    printMessage(client);
                }

                keys.remove();
            }


        }



    }


}
