package ro.agitman.atalk.model;

import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Created by edi on 3/22/2016.
 */
public class TextMsgEncoder implements Encoder.Text<TextMsg>  {

    public String encode(TextMsg textMsg) throws EncodeException {
        Gson gson = new Gson();
        return gson.toJson(textMsg);
    }

    public void init(EndpointConfig endpointConfig) {
    }

    public void destroy() {
    }
}
