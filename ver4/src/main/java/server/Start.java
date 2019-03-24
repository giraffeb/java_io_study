package server;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;
import server.impl.GenericServerStarter;

import java.io.IOException;

/**
 * ChatServer 시작점
 */
public class Start {

    static Logger LOG = (Logger) LoggerFactory.getLogger(Start.class);



    public static void main(String[] args) {
        LOG.setLevel(Level.WARN);
        try {

            GenericServerStarter genericServerStarter = new GenericServerStarter(4444);
            genericServerStarter.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
