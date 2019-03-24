package server.interfaces;

import java.io.Serializable;

public interface Message extends Serializable {

    int HEADER_SIZE = Integer.BYTES;
    int MESSAGE_BUFFER_SIZE = 1024 * 1024 * 2; //2MB


}
