import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;
import java.util.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SessionHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Get the request method (POST, GET, etc.)
        String requestMethod = exchange.getRequestMethod();

        UrlOperation operation = analizeUrl(exchange.getRequestURI().getPath());
        if ("POST".equals(requestMethod)) {
            if(operation.action == UrlAction.LOGIN){
				Map<String, String> jsonMap = Server.requestJson(exchange);
                String name = jsonMap.get("userName");
                String key = jsonMap.get("publicKey");
                String token = "";

                int authenticationMethod = Server.getAuthenticationMethod(name);

                switch(authenticationMethod){
                case 0:
                    String password = jsonMap.get("passwd");
                    token = Server.authenticate(name,password,key.getBytes());
                    if(!token.equals("")){
                        Server.response(exchange, 200, token, false);
                    }else{
                        Server. response(exchange, 401, "Incorrect username or password", false);
                    }
                    break;
                case 1:
                    String passwordPart0 = jsonMap.get("passPart0");
                    String passwordPart1 = jsonMap.get("passPart1");
                    String passwordPart2 = jsonMap.get("passPart2");

                    token = Server.authenticate(name,passwordPart0,passwordPart1,passwordPart2,key.getBytes());

                    if(token.equals("0")){
                        Server.response(exchange, 202, token, false);
                    }else if(!token.equals("")){
                        Server.response(exchange, 200, token, false);
                    }else{
                        Server. response(exchange, 401, "Incorrect username or password", false);
                    }
                    break;
                default:
                    Server.response(exchange, 401, "User does not exist", false);
                    break;
                }
            }else if(operation.action == UrlAction.LOGOUT){
				Map<String, String> jsonMap = Server.requestJson(exchange);
                String token = jsonMap.get("sessionToken");
                if(SessionManager.invalidateSessionToken(token)){
                    Server.response(exchange, 200, "Logout", false);
                }else{
                    Server.response(exchange, 401, "Incorrect token", false);
                }
            }else if(operation.action == UrlAction.TEST){
                //String encryptedData = jsonMap.get("data");
				//System.out.println("DATA - " + encryptedData);
				//byte[] data = Server.requestBinary(exchange);
				InputStream is = exchange.getRequestBody();
				//byte[] dataB = Base64.getDecoder().decode(is.readAllBytes());
				String data = new String(is.readAllBytes(), StandardCharsets.UTF_8);
				System.out.println("hola");
				byte[] dataB = Base64.getDecoder().decode(data);
				data = dataB.toString();
                System.out.println("DATA - " + data);
				String decryptedData = Encrypt.decrypt(data.getBytes());
                System.out.println(decryptedData);
                Server.response(exchange,200,decryptedData,false);
            } else{
                // Send a response 
                Server.response(exchange,400,"Received POST request at /session with invalid format",false);
            }
        }else if ("GET".equals(requestMethod)) {
            if(operation.action == UrlAction.PUBLICKEY){
                System.out.println("sending public key");
                byte[] bytes = Encrypt.getPublicBytes();
                Server.response(exchange,200,bytes,bytes.length);
            }else{
                // Send a response 
                Server.response(exchange,400,"Received GET request at /session with invalid format",false);
            }
        }else{
            // Handle other HTTP methods or provide an error response
            Server.response(exchange,405,"Unsupported HTTP method",false);
        }
    }

    private UrlOperation analizeUrl(String path){
        UrlOperation operation = new UrlOperation(0, UrlAction.ERROR);

        if(Utils.compareURL(path, "/session/login")!=-2) operation.set(-1,UrlAction.LOGIN);
        if(Utils.compareURL(path, "/session/logout")!=-2) operation.set(-1,UrlAction.LOGOUT);
        if(Utils.compareURL(path, "/session/public")!=-2) operation.set(-1,UrlAction.PUBLICKEY);
        if(Utils.compareURL(path, "/session/test")!=-2) operation.set(-1,UrlAction.TEST);// ! TOREMOVE

        return operation;
    }
}