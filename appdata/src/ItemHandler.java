import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;
import java.io.IOException;

public class ItemHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Get the request method (POST, GET, etc.)
        String requestMethod = exchange.getRequestMethod();

        UrlOperation operation = analizeUrl(exchange.getRequestURI().getPath());
        Map<String, String> jsonMap = Server.requestJson(exchange);
        if ("POST".equals(requestMethod)) {
            switch(operation.action){
            case NEW_ITEM:
                String newName = jsonMap.get("name");
                int newImage = Server.string2id(jsonMap.get("image"));
                int newCount = Server.string2id(jsonMap.get("count"));
                Server.createItem(newName, newImage,newCount);
                Server.response(exchange,200,"Received POST request at /item/new to create item");         
                break;
            case ITEM:
                String name = jsonMap.get("name");
                int image = Server.string2id(jsonMap.get("image"));
                int count = Server.string2id(jsonMap.get("count"));
                Server.updateItem(operation.id, name, image,count);
                Server.response(exchange, 200, "Received POST request at /item to update item");
                break;
            default:
                Server.response(exchange, 400, "Received POST request with invalid format");
                break;
            }
        }else if("GET".equals(requestMethod)){
            switch(operation.action){
            case ALL_ITEMS:
                String items = Utils.multipleItemsToJson(Server.getAllItems());
                System.out.println(items);
                Server.response(exchange, 200, items);
                break;
            case ITEM:
                String item = Utils.itemToJson(Server.getItem(operation.id));
                Server.response(exchange, 200, item);
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
        
        n = Utils.compareURL(path, "/item/?"); if(n!=-2) operation.set(n,UrlAction.ITEM);
        if(Utils.compareURL(path, "/item/new")!=-2) operation.set(-1,UrlAction.NEW_ITEM);
        if(Utils.compareURL(path, "/item")!=-2) operation.set(-1,UrlAction.ALL_ITEMS);
        n = Utils.compareURL(path, "/item/delete/?"); if(n!=-2) operation.set(n,UrlAction.DELETE_ITEM);//TODO

        return operation;
    }
}
