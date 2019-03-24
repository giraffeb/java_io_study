package server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.impl.controller.RequestMessageParser;
import server.impl.io.SimpleMessageFactory;
import server.impl.repository.SimpleMessageQueue;
import server.impl.vo.message.RequestMessage;
import server.interfaces.Message;
import server.interfaces.ServerStarter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * ServerSocketChannel을 초기화하고,
 * 클라이언트 SocketChannel을 새로 등록하고 입력을 처리합니다.
 */
public class GenericServerStarter implements ServerStarter {

    private static Logger LOG = LoggerFactory.getLogger(GenericServerStarter.class);

    private int serverPort;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private SimpleMessageQueue simpleMessageQueue;


    private SimpleMessageFactory simpleMessageFactory;
    private RequestMessageParser requestMessageParser;


    /**
     * TODO: 객체생성을 어디서 해주는게 적절할까?
     * 전체적으로 필요한 객체들을 여기서 생성해주는데, 위치가 적절한거 같지는 않다.
     *
     * @throws IOException
     */
    @Override
    public void init() throws IOException {

        this.selector = Selector.open();

        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.bind(new InetSocketAddress(this.serverPort));
        this.serverSocketChannel.configureBlocking(false);

        this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        this.simpleMessageQueue = new SimpleMessageQueue();

        this.simpleMessageFactory = new SimpleMessageFactory();
        this.requestMessageParser = new RequestMessageParser();


    }

    /**
     * 필요한 객체들을 초기화하고, 사용자에게 받을 포트 정보를 입력한다.
     * @param serverPort
     * @throws IOException
     */
    public GenericServerStarter(int serverPort) throws IOException {
        LOG.debug("CONSTRUCTOR");
        this.serverPort = serverPort;
        init();
    }

    /**
     * select를 사용해서 요청되는 socketChannel을 등록하고
     * 입력을 받아들여서 하나의 메시지 단위로 만드는 역할을 수행함.
     *
     * 다른 비동기 서버에서 본것 처럼 싱글쓰레드로 처리하고
     * 로직이 필요한 부분을 멀티 쓰레드로 처리하자.
     * @throws IOException
     */
    @Override
    public void start() throws IOException {
        LOG.debug("START");
        int selectCount = 0;
        Set<SelectionKey> selectedKeys;
        Iterator<SelectionKey> iteratorSelectionKeys;
        SelectionKey curSelectionKey;

        SocketChannel curSocketChannel;
        ByteBuffer curByteBuffer;


        while(true){
            selectCount = this.selector.selectNow();

            if(selectCount == 0){
                continue;
            }

            selectedKeys = this.selector.selectedKeys();
            iteratorSelectionKeys = selectedKeys.iterator();

            while(iteratorSelectionKeys.hasNext()){

                curSelectionKey = iteratorSelectionKeys.next();

                if(curSelectionKey.isAcceptable()){
                    /**
                     * 요청이 들어옹 소켓채널과 바이트 버퍼를 생성하고, selector에 등록함.
                     */
                    LOG.debug("ACCEPTABLE");

                    curSocketChannel = ((ServerSocketChannel)curSelectionKey.channel()).accept();
                    curSocketChannel.configureBlocking(false);
                    curSocketChannel.register(this.selector, SelectionKey.OP_READ, ByteBuffer.allocate(Message.MESSAGE_BUFFER_SIZE));

                }else if(curSelectionKey.isReadable()){
                    /**
                     * 데이터(RequestMessage)가 들어온 소켓채널과 바이트버퍼를 selector로부터 가져와서 로직을 부여한다.
                     * TODO: RequestMessage를 JobQueue에 담아서 멀티 쓰레드로 처리가능하도록 하자.
                     */
                    LOG.debug("READABLE");

                    curSocketChannel = (SocketChannel) curSelectionKey.channel();
                    curByteBuffer = (ByteBuffer) curSelectionKey.attachment();

                    RequestMessage requestMessage = this.simpleMessageFactory.readRequestMessage(curSocketChannel, curByteBuffer);
                    if(requestMessage == null){
                        /**
                         * 소켓 연결이 종료되었거나, 메시지가 정상적으로 들어오지 않은 경우.
                         */
                        LOG.debug("Socket Closed or No Data read");
                        break;
                    }
                    /**
                     * 이 메시지를 보낸 사용자의 ip:port번호를 부여함.
                     */
                    requestMessage.setSenderSocketChannel(curSocketChannel)
                            .setByteBuffer(curByteBuffer);

                    /**
                     * 받은 메시지 출력
                     */
                    LOG.debug(requestMessage.toString());

                    /**
                     * requestMessage 처리하는 로직 호출
                     */
                    this.requestMessageParser.parseMessage(requestMessage);

                }else if(!curSelectionKey.isValid()){
                    curSelectionKey.cancel();
                }

                /**
                 * iteratorSelectionKeys에서 사용된 SelectionKey를 제거함.
                 */
                iteratorSelectionKeys.remove();

            }

        }
    }

    @Override
    public void destroy() {
        //정상종료시 저장 처리 등을 위해서 합니다.
    }
}
