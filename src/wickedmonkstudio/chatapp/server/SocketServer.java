package wickedmonkstudio.chatapp.server;

import com.google.common.collect.Maps;
import org.json.JSONException;
import org.json.JSONObject;
import wickedmonkstudio.chatapp.JSONUtils;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;



@ServerEndpoint("/chat")
public class SocketServer {
    private static final Set<Session> sessionsSet
            = Collections.synchronizedSet(new HashSet<Session>());

    private static final HashMap<String, String> nameSessionPair = new HashMap<>();

    private JSONUtils jsonUtils=new JSONUtils();

    public static Map<String, String> getQueryMap(String query){
        Map<String, String> map = Maps.newHashMap();
        if(query !=null){
            String[] params = query.split("&");
            for(String param : params){
                String[] nameval = param.split("=");
                map.put(nameval[0], nameval[1]);
            }
        }
        return map;
    }

    /**
     * Called when socket connection is opened
     * @param session
     *          session
     */
    @OnOpen
    public void onOpen(Session session){
        System.out.println(session.getId() + " has opened a connection.");
        Map<String, String> queryParams = getQueryMap(session.getQueryString());

        String name = "";

        if(queryParams.containsKey("name")){
            try{
                name = URLDecoder.decode(name, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            nameSessionPair.put(session.getId(), name);
        }
        sessionsSet.add(session);

        try {
            session.getBasicRemote().sendText(jsonUtils.getClientDetailsJson(session.getId(), "Your session details"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendMessageToAll(session.getId(), name, " joined conversation.", true, true);
    }

    /**
     * Method called when new message received from any client
     * @param message
     *          Json message from client
     * @param session
     *          session
     */
    @OnMessage
    public void onMessage(String message, Session session){
        System.out.println("Message from "+session.getId()+": "+message);
        String msg =null;

        try {
            JSONObject jsonObject = new JSONObject(message);
            msg=jsonObject.getString(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendMessageToAll(session.getId(), nameSessionPair.get(session.getId()), msg, false, false);
    }

    /**
     * Method called when a connection is closed
     * @param session
     *          session
     */
    @OnClose
    public void onClose(Session session){
        System.out.println("Session "+session.getId() + " has ended");

        String name = nameSessionPair.get(session.getId());
        sessionsSet.remove(session);

        sendMessageToAll(session.getId(), name, " left conversation.", false, true);
    }

    /**
     *
     * @param sessionId
     *          session id
     * @param name
     *          client name
     * @param message
     *          message to be send to client
     * @param isNewClient
     *          flag to identify that message is about new person joined
     * @param isExit
     *          flag to identify that client left the conversation
     */
    private void sendMessageToAll(String sessionId, String name, String message, boolean isNewClient, boolean isExit) {

        for(Session session : sessionsSet){
            String json;
            if(isNewClient){
                json=jsonUtils.getNewClientJson(sessionId, name, message, sessionsSet.size());

            }else if(isExit){
                json = jsonUtils.getClientExitJson(sessionId, name, message, sessionsSet.size());
            }else{
                json=jsonUtils.getSendAllMessageJson(sessionId, name, message);
            }

            try {
                System.out.println("Sending Message To: "+sessionId + ", " + json);
                session.getBasicRemote().sendText(json);

            } catch (IOException e) {
                System.err.println("Error in sending "+session.getId()+", "+e.getMessage());
                // e.printStackTrace();
            }
        }
    }


}
