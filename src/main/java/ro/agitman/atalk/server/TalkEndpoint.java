package ro.agitman.atalk.server;

import com.google.gson.Gson;
import ro.agitman.atalk.model.TextMsg;
import ro.agitman.atalk.model.TextMsgDecoder;
import ro.agitman.atalk.model.TextMsgEncoder;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@ServerEndpoint(value = "/talk/{clientId}", encoders = {TextMsgEncoder.class}, decoders = {TextMsgDecoder.class})
public class TalkEndpoint {

    @OnOpen
    public void onOpen(@PathParam("clientId") String clientId, Session session) {
        MemCash.getInstance().getSessions().put(session, clientId);
        MemCash.getInstance().addColor(clientId);
        Gson gson = new Gson();
        try {
            TextMsg msg = new TextMsg();
            msg.setType("connect");
            msg.setSender(clientId);

            DbAccess.getInst().save(msg);

            msg.getUsers().addAll(MemCash.getInstance().getSessions().values());

            msg.getTodays().addAll(fullInfoMessages());

            session.getBasicRemote().sendObject(gson.toJson(msg));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(TextMsg msg, Session session) throws IOException {
        if ("ping".equals(msg.getType())) {
            TextMsg response = new TextMsg();
            response.setType("po");
            response.getUsers().addAll(MemCash.getInstance().getSessions().values());
            try {
                session.getBasicRemote().sendObject(response);
            } catch (EncodeException e) {
                e.printStackTrace();
            }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM HH:mm:ss");
            msg.setDate(sdf.format(new Date()));
            DbAccess.getInst().save(msg);
            msg.setColor(MemCash.getInstance().getColors().get(msg.getSender()));
            for (Session ses : session.getOpenSessions()) {
                try {
                    ses.getBasicRemote().sendObject(msg);
                } catch (EncodeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        MemCash.getInstance().getSessions().remove(session);
        System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
    }

    private List<TextMsg> fullInfoMessages() {

        List<TextMsg> messages = DbAccess.getInst().getToday();

        for (TextMsg message : messages) {
            message.setColor(MemCash.getInstance().getColors().get(message.getSender()));
        }

        return messages;
    }


}
