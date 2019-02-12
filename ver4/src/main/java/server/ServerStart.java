package server;

import server.implement.GenericIOConnector;

import java.io.IOException;

public class ServerStart {
    public static void main(String[] args) throws IOException, NoSuchMethodException {
        GenericIOConnector io = new GenericIOConnector(4444);

        io.init();
        io.start();

    }
}
