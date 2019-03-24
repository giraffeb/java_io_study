package server.interfaces;

import java.io.IOException;

public interface ServerStarter {

    void init() throws IOException;
    void start() throws IOException;
    void destroy();
}
