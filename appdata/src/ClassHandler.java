import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;
import java.io.IOException;

public class ClassHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Get the request method (POST, GET, etc.)
        String requestMethod = exchange.getRequestMethod();

        UrlOperation operation = analizeUrl(exchange.getRequestURI().getPath());
        Map<String, String> jsonMap = Server.requestJson(exchange);
       if("GET".equals(requestMethod)){
            switch(operation.action){
            case ALL_CLASSES:
                String items = Utils.multipleClassroomsToJson(Server.getClassList());
                System.out.println(items);
                Server.response(exchange, 200, items);
                break;
            default:
                Server.response(exchange, 400, "Received GET request with invalid format");
                break;
            }
        }else{
            // Handle other HTTP methods or provide an error response
            Server.response(exchange,405,"Unsupported HTTP method");           
        }
    }

    private UrlOperation analizeUrl(String path){
        UrlOperation operation = new UrlOperation(0, UrlAction.ERROR);

        int n = -2;
        
        if(Utils.compareURL(path, "/class")!=-2) operation.set(-1,UrlAction.ALL_CLASSES);

        return operation;
    }
}

