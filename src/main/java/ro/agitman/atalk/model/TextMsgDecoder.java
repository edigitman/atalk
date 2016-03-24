package ro.agitman.atalk.model;

import com.google.gson.Gson;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * Created by edi on 3/22/2016.
 */
public class TextMsgDecoder implements Decoder.Text<TextMsg> {

    public TextMsg decode(String s) throws DecodeException {
        Gson gson = new Gson();
        return gson.fromJson(s, TextMsg.class);
    }

    public boolean willDecode(String s) {
        return false;
    }

    public void init(EndpointConfig endpointConfig) {

    }

    public void destroy() {

    }
}
