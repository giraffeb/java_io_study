package server;

public class ServerStart {

    /*
    * 사용자의 접속마다 입력, 출력 쓰레드를 각각 1개씩 만드는
    * 전체 채팅 에코프로그램을 목적으로 합니다.
    * */
    public static void main(String[] args) throws Exception {
        Server server = new Server(4444);
        server.doProcess();

    }
}
