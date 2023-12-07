import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class GalleryHandler implements HttpHandler {
    boolean fileUploaded = false;
    byte[] lastData;
    String lastFileName;
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Get the request method (POST, GET, etc.)
        String requestMethod = exchange.getRequestMethod();

        UrlOperation operation = analizeUrl(exchange.getRequestURI().getPath());
        // curl -i -Ffiledata=@test.png -Fdata='{"username":"user1", "password":"password"}'  http://localhost:8080/gallery/new
        // curl -o dl.png  http://localhost:8080/gallery

        if ("POST".equals(requestMethod)) {
            switch(operation.action){
            case NEW_MEDIA:
                ArrayList<MultipartSection> sections = Server.getSections(exchange);
                for(int i = 0; i < sections.size();i++){
                    MultipartSection section = sections.get(i);
                    String name = section.getTag("name");
                    if(name.equals("filedata")){
                        lastData = section.getData();
                        lastFileName = section.getTag("filename");
                    }
                }

                System.out.println("new media");
                fileUploaded = true;
                Server.response(exchange, 200, "new media");
                
                break;
            default:
                Server.response(exchange, 400, "Received POST request with invalid format");
                break;
            }
        }else if ("GET".equals(requestMethod)){
            switch(operation.action){
            case ALL_MEDIA:
                if(fileUploaded){
                    System.out.println("getting media");
                    Server.responseFile(exchange, 200, lastData,lastFileName);
                }else{
                    System.out.println("no file media");
                    Server.response(exchange, 400, "no file media");
                }

                
                break;
            default:
                Server.response(exchange, 400, "Received POST request with invalid format");
                break;
            }
        }else{
            Server.response(exchange, 400, "Unsupported method");
        }
    }

    private UrlOperation analizeUrl(String path){
        UrlOperation operation = new UrlOperation(0, UrlAction.ERROR);

        int n = -2;
        // TODO
        n = Utils.compareURL(path, "/gallery/?"); if(n!=-2) operation.set(n,UrlAction.MEDIA);
        if(Utils.compareURL(path, "/gallery/new")!=-2) operation.set(-1,UrlAction.NEW_MEDIA);
        if(Utils.compareURL(path, "/gallery")!=-2) operation.set(-1,UrlAction.ALL_MEDIA);
        n = Utils.compareURL(path, "/gallery/delete/?"); if(n!=-2) operation.set(n,UrlAction.DELETE_MEDIA);

        return operation;
    }
}
