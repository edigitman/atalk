package com.javacodegeeks.snippets.enterprise.jettywebsocket.jsr356.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/talk/{clientId}")
public class TalkEndpoint {

    @OnOpen
    public void onOpen(@PathParam("clientId") String clientId, Session session) {
        MemCash.getInstance().getSessions().put(session, clientId);
    }

    @OnMessage
    public void onMessage(String txt, Session session) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM HH:mm:ss");
        Date date = new Date();
        for (Session ses : session.getOpenSessions()) {
            String sender = MemCash.getInstance().getSessions().get(session);
            ses.getBasicRemote().sendText("[" + sdf.format(date) + "] " + sender + ": " + txt);
        }
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        MemCash.getInstance().getSessions().remove(session);
        System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
    }
}
