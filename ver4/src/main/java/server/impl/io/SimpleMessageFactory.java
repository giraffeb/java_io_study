package server.impl.io;



import server.impl.vo.message.RequestMessage;
import server.impl.vo.message.ResponseMessage;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


/**
 * Request, Response Message를 serialize해서
 */
public class SimpleMessageFactory extends SimpleMessageIO{

    ObjectOutputStream objectOutputStream;
    ByteArrayOutputStream byteOutputStream;

    ObjectInputStream objectInputStream;
    ByteArrayInputStream byteInputStream;


    public void objectWriteInit(){
        this.byteOutputStream = new ByteArrayOutputStream();
        try {
            this.objectOutputStream = new ObjectOutputStream(this.byteOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public SimpleMessageFactory() {
        super();
    }

    public void sendRequestMessage(SocketChannel socketChannel, ByteBuffer byteBuffer, RequestMessage requestMessage){
        byte[] messageBytes = this.requestMessageToByteArray(requestMessage);
        this.sendMessage(socketChannel, byteBuffer, messageBytes);
    }

    public RequestMessage readRequestMessage(SocketChannel socketChannel, ByteBuffer byteBuffer){
        byte[] messageBytes = this.readMessage(socketChannel, byteBuffer);
        if(messageBytes == null){
            return null;
        }

        RequestMessage requestMessage = this.byteArrayToRequestMessage(messageBytes);

        return requestMessage;
    }

    public RequestMessage byteArrayToRequestMessage(byte[] bytes){
         RequestMessage requestMessage = null;

        try {
            this.byteInputStream = new ByteArrayInputStream(bytes);
            this.objectInputStream = new ObjectInputStream(this.byteInputStream);
            requestMessage = (RequestMessage) this.objectInputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return requestMessage;
    }

    public byte[] requestMessageToByteArray(RequestMessage requestMessage){
        objectWriteInit();
        byte[] bytes = null;

        try {
            this.objectOutputStream.writeObject(requestMessage);
            bytes = this.byteOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public void sendResponseMessage(SocketChannel socketChannel, ByteBuffer byteBuffer, ResponseMessage responseMessage){
        byte[] messageBytes = this.responseMessageToByteArray(responseMessage);
        this.sendMessage(socketChannel, byteBuffer, messageBytes);
    }

    public ResponseMessage readResponseMessage(SocketChannel socketChannel, ByteBuffer byteBuffer){
        byte[] messageBytes = this.readMessage(socketChannel, byteBuffer);
        if(messageBytes == null){
            return null;
        }
        ResponseMessage responseMessage = this.byteArrayToResponseMessage(messageBytes);

        return responseMessage;
    }

    public ResponseMessage byteArrayToResponseMessage(byte[] bytes){
        ResponseMessage responseMessage = null;

        try {
            this.byteInputStream = new ByteArrayInputStream(bytes);
            this.objectInputStream = new ObjectInputStream(this.byteInputStream);
            responseMessage = (ResponseMessage) this.objectInputStream.readObject();
            this.objectInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return responseMessage;
    }

    public byte[] responseMessageToByteArray(ResponseMessage responseMessage){
        //이코드로 작동확인완료
        objectWriteInit();

        byte[] bytes = null;
        try {
            this.objectOutputStream.writeObject(responseMessage);
            bytes = this.byteOutputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                this.byteOutputStream.close();
                this.objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }


}
