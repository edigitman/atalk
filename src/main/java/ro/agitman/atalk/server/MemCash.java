package ro.agitman.atalk.server;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by d-uu31cq on 22.03.2016.
 */
public class MemCash {

    private String[] predefineColors = new String[]{
            "darkblue",
            "darkorange",
            "darkturqoise",
            "darkmagenta",
            "darkcyan",
            "darkgray",
            "darkgreen",
            "darkred",
            "darkviolet"
    };
    private static MemCash instance = new MemCash();

    private Map<Session, String> sessions = new HashMap<Session, String>();
    private Map<String, String> colors = new HashMap<String, String>();

    private MemCash() {
    }

    public static MemCash getInstance() {
        return instance;
    }

    public Map<Session, String> getSessions() {
        return sessions;
    }

    public Map<String, String> getColors() {
        return colors;
    }

    public void addColor(String userId) {
        colors.put(userId, findColor());
    }

    private String findColor() {

        for (String color : predefineColors) {
            if (!colors.values().contains(color)) {
                return color;
            }
        }
        return "black";
    }
}
