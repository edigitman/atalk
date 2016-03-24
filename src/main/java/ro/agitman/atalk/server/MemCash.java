package ro.agitman.atalk.server;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by d-uu31cq on 22.03.2016.
 */
public class MemCash {

    private static MemCash instance = new MemCash();

    private Map<Session, String> sessions = new HashMap<Session, String>();

    private MemCash(){}

    public static MemCash getInstance(){
        return instance;
    }

    public Map<Session, String> getSessions() {
        return sessions;
    }
}
