import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class SessionManager {
    private static Map<String, Session> sessionTokenToUser = new HashMap<>();
    
    public static String createSessionToken(int userId,byte[] key) {
        String sessionToken = UUID.randomUUID().toString();
        // sessionTokenToUser.put(sessionToken, new Session(userId, Encrypt.getKeyFromBytes(key))); 
        sessionTokenToUser.put(sessionToken, new Session(userId, Encrypt.genKeys())); // ! codigo temporal mientras la encriptacion no esté 100%
        return sessionToken;
    }
    
    public static int getUserFromSessionToken(String sessionToken) {
        return sessionTokenToUser.get(sessionToken).getId();
    }
    
    public static boolean invalidateSessionToken(String sessionToken) {
        return(sessionTokenToUser.remove(sessionToken) != null);
    }

    public static String findSessionTokenByUser(int userId) {
        for (Map.Entry<String, Session> entry : sessionTokenToUser.entrySet()) {
            if (entry.getValue().getId() == userId) {
                return entry.getKey();
            }
        }
        return null; // Return null if no session token is found for the given username
    }
}