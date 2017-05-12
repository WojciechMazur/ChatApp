package wickedmonkstudio.chatapp;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * Created by Wojciech on 02.05.2017.
 */
public class JSONUtils {
    // flags to identify the kind of json response on client side
    private static final String FLAG_SELF = "self", FLAG_NEW = "new",
            FLAG_MESSAGE = "message", FLAG_EXIT = "exit";

    public JSONUtils() {
    }

    /**
     * Json when client needs it's own session details
     * */
    public String getClientDetailsJson(String sessionId, String message) {
        String json = null;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("flag", FLAG_SELF);
            jsonObject.put("sessionId", sessionId);
            jsonObject.put("message", message);

            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * Json to notify all the clients about new person joined
     * */
    public String getNewClientJson(String sessionId, String name,
                                   String message, int onlineCount) {
        String json = null;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("flag", FLAG_NEW);
            jsonObject.put("name", name);
            jsonObject.put("sessionId", sessionId);
            jsonObject.put("message", message);
            jsonObject.put("onlineCount", onlineCount);

            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * Json when the client exits the socket connection
     * */
    public String getClientExitJson(String sessionId, String name,
                                    String message, int onlineCount) {
        String json = null;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("flag", FLAG_EXIT);
            jsonObject.put("name", name);
            jsonObject.put("sessionId", sessionId);
            jsonObject.put("message", message);
            jsonObject.put("onlineCount", onlineCount);

            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * JSON when message needs to be sent to all the clients
     * */
    public String getSendAllMessageJson(String sessionId, String fromName,
                                        String message) {
        String json = null;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("flag", FLAG_MESSAGE);
            jsonObject.put("sessionId", sessionId);
            jsonObject.put("name", fromName);
            jsonObject.put("message", message);

            json = jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;

    }
}
