import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class SessionManager {
    private static Map<String, Integer> sessionTokenToUser = new HashMap<>();
    
    public static String createSessionToken(int userId) {
        String sessionToken = UUID.randomUUID().toString();
        sessionTokenToUser.put(sessionToken, userId);
        return sessionToken;
    }
    
    public static int getUserFromSessionToken(String sessionToken) {
        return sessionTokenToUser.get(sessionToken);
    }
    
    public static boolean invalidateSessionToken(String sessionToken) {
        return(sessionTokenToUser.remove(sessionToken) != null);
    }

    public static String findSessionTokenByUser(int userId) {
        for (Map.Entry<String, Integer> entry : sessionTokenToUser.entrySet()) {
            if (entry.getValue() == userId) {
                return entry.getKey();
            }
        }
        return null; // Return null if no session token is found for the given username
    }
}