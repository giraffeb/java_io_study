package server;

public class ServerStart {
    public static void main(String[] args) {
        GenericServerStarter server = new GenericServerStarter(4444);

        server.init();
        server.start();

    }
}
