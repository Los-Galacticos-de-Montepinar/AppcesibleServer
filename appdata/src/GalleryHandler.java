import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;
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
        Headers headers = exchange.getRequestHeaders();
        List<String> ct = headers.get("content-type");
        String contentType = "";
        if(ct!=null){
            System.out.println(headers);

            contentType = ct.get(0).split(";")[0];
        }

        if ("POST".equals(requestMethod)) {
            switch(operation.action){
            case NEW_MEDIA:
                if(contentType.equals("multipart/form-data")){
                    
                    ArrayList<MultipartSection> sections = Server.getSections(exchange);
                    for(MultipartSection section : sections){
                        String name = section.getTag("name");
                        if(name.equals("filedata")){
                            System.out.println(section.getContentType());
                            if(
                                section.getContentType().equals("image/png")||
                                section.getContentType().equals("image/jpeg")||
                                section.getContentType().equals("image/jpg")){

                                byte[] data = section.getData();

                                int id = Server.createImage(section.getTag("filename"), data);
                                System.out.println("new image");
                                Server.response(exchange, 200, ""+id);
                            }else{
                                Server.response(exchange, 400, "not a valid file extension");
                            }
                        }

                        if(name.equals("data")){
                            if(section.getContentType().equals("application/json")){
                                Map<String, String> jsonMap = Server.parseJson(new String(section.getData()));
                                int mediaType = Server.string2id(jsonMap.get("type"));
                                if(mediaType>=0&&mediaType<=1){
                                    Server.createImage(name, lastData,mediaType);
                                }
                            }
                        }
                    }
                }else{
                    Server.response(exchange, 400, "not a valid content type");
                }

                break;
            default:
                Server.response(exchange, 400, "Received POST request with invalid format");
                break;
            }
        }else if ("GET".equals(requestMethod)){
            switch(operation.action){
            case ALL_MEDIA:
                Server.response(exchange, 200, Utils.multipleMediaMetadataToJson(Server.getMediaList()));
                break;
            case MEDIA:
                ResultSet media = Server.getMedia(operation.id);
                Server.responseFile(exchange,200, media);
                break;
            case ALL_IMAGES:
                Server.response(exchange, 200, Utils.multipleMediaMetadataToJson(Server.getImageList()));
                break;
            case ALL_PICTOGRAMS:
                Server.response(exchange, 200, Utils.multipleMediaMetadataToJson(Server.getPictogramList()));
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

        n = Utils.compareURL(path, "/gallery/?"); if(n!=-2) operation.set(n,UrlAction.MEDIA);
        if(Utils.compareURL(path, "/gallery/new")!=-2) operation.set(-1,UrlAction.NEW_MEDIA);
        if(Utils.compareURL(path, "/gallery")!=-2) operation.set(-1,UrlAction.ALL_MEDIA);
        if(Utils.compareURL(path, "/gallery/image")!=-2) operation.set(-1,UrlAction.ALL_IMAGES);
        if(Utils.compareURL(path, "/gallery/pictogram")!=-2) operation.set(-1,UrlAction.ALL_PICTOGRAMS);
        n = Utils.compareURL(path, "/gallery/delete/?"); if(n!=-2) operation.set(n,UrlAction.DELETE_MEDIA);

        return operation;
    }
}
