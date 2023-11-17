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
                String token = "";

                int authenticationMethod = Server.getAuthenticationMethod(name);

                switch(authenticationMethod){
                case 0:
                    String password = jsonMap.get("passwd");
                    token = Server.authenticate(name,password);
                    if(!token.equals("")){
                        Server.response(exchange, 200, token);
                    }else{
                        Server. response(exchange, 401, "Incorrect username or password");
                    }
                    break;
                case 1:
                    String passwordPart0 = jsonMap.get("passPart0");
                    String passwordPart1 = jsonMap.get("passPart1");
                    String passwordPart2 = jsonMap.get("passPart2");

                    token = Server.authenticate(name,passwordPart0,passwordPart1,passwordPart2);

                    if(token.equals("0")){
                        Server.response(exchange, 202, token);
                    }else if(!token.equals("")){
                        Server.response(exchange, 200, token);
                    }else{
                        Server. response(exchange, 401, "Incorrect username or password");
                    }
                    break;
                default:
                    Server.response(exchange, 401, "User does not exist");
                    break;
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

        if(Utils.compareURL(path, "/session/login")!=-2) operation.set(-1,UrlAction.LOGIN);
        if(Utils.compareURL(path, "/session/logout")!=-2) operation.set(-1,UrlAction.LOGOUT);

        return operation;
    }
}