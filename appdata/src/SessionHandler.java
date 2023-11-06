import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;
import java.io.IOException;

public class SessionHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Get the request method (POST, GET, etc.)
        String requestMethod = exchange.getRequestMethod();

        UrlOperation operation = analizeUrl(exchange.getRequestURI().getPath());
        Map<String, String> jsonMap = Server.requestJson(exchange);
        if ("POST".equals(requestMethod)) {
            if(operation.action == UrlAction.LOGIN){
                String name = jsonMap.get("userName");
                String password = jsonMap.get("passwd");
                String token = Server.authenticate(name,password);
                if(!token.equals("")){
                    Server.response(exchange, 200, token);
                }else{
                   Server. response(exchange, 401, "Incorrect username or password");
                }
            }else if(operation.action == UrlAction.LOGOUT){
                String token = jsonMap.get("sessionToken");
                if(SessionManager.invalidateSessionToken(token)){
                    Server.response(exchange, 200, "Logout");
                }else{
                    Server.response(exchange, 401, "Incorrect token");
                }
            } else{
                // Send a response 
                Server.response(exchange,400,"Received POST request at /session with invalid format");
            }
        }else {
            // Handle other HTTP methods or provide an error response
            Server.response(exchange,405,"Unsupported HTTP method");
        }
    }

    private UrlOperation analizeUrl(String path){
        UrlOperation operation = new UrlOperation(0, UrlAction.ERROR);
        try {
            String[] parts = path.split("/");
            int size = parts.length;
            String idString = parts[size-1];

            if(size==3){
                if(parts[1].equals("session")){
                    if(idString.equals("login")) operation.set(0,UrlAction.LOGIN);// Login
                    else if(idString.equals("logout")) operation.set(0,UrlAction.LOGOUT);
                }
            }

        }catch(NumberFormatException e){
            return operation; //Invalid URL format
        }
        return operation;
    }
}