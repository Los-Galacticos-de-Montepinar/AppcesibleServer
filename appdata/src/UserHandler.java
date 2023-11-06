import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;
import java.io.IOException;

public class UserHandler implements HttpHandler {
    // ! Por tal de simplificar la comunicacion por ahora, ninguna operacion requiere un token sesión
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Get the request method (POST, GET, etc.)
        String requestMethod = exchange.getRequestMethod();

        UrlOperation operation = analizeUrl(exchange.getRequestURI().getPath());
        System.out.println(operation.action);
        if ("POST".equals(requestMethod)) {
            if(operation.action == UrlAction.USER){
                // Parse the JSON payload manually
                Map<String, String> jsonMap = Server.requestJson(exchange);

                if(jsonMap!=null){
                    // Get fields from the JSON payload
                    String name = jsonMap.get("userName");
                    String passwd = jsonMap.get("passwd");
                    int idPFP = Server.string2id(jsonMap.get("pfp"));
                    int idClass = Server.string2id(jsonMap.get("idClass"));
                    
                    int letterSize = Server.string2id(jsonMap.get("letterSize"));
                    int loginType = Server.string2id(jsonMap.get("loginType"));
                    int interactionFormat = Server.string2id(jsonMap.get("interactionFormat"));

                    Server.updateUser(operation.id, name, passwd, idPFP,idClass,letterSize,loginType,interactionFormat);

                    // Send a response 
                    Server.response(exchange, 200, "Received POST request at /user/"+operation.id+ " to update user");
                } else {
                    Server.response(exchange, 400, "Received POST request with invalid format");
                }

            } else if(operation.action == UrlAction.NEW_USER){
                // Add new user

                // Parse the JSON payload manually
                Map<String, String> jsonMap = Server.requestJson(exchange);

                // Get the fields from the JSON payload
                String name = jsonMap.get("userName");
                int pfp = Integer.parseInt(jsonMap.get("pfp"));
                int idClass = Integer.parseInt(jsonMap.get("idClass"));
                String pass = jsonMap.get("passwd");
                int type = Integer.parseInt(jsonMap.get("userType"));

                int studentUserId = Server.createUser(name, pass, idClass, type, pfp);
                if(studentUserId>=0){
                    int letterSize = Integer.parseInt(jsonMap.get("letterSize"));
                    int loginType = Integer.parseInt(jsonMap.get("loginType"));
                    int interactionFormat = Integer.parseInt(jsonMap.get("interactionFormat"));
                    Server.createStudent(studentUserId,letterSize , loginType, interactionFormat);
                }

                // Send a response 
                Server.response(exchange, 200, "Received POST request at /user/new to create new user");
            } else if (operation.action == UrlAction.DELETE_USER){
                // Parse the JSON payload manually
                Map<String, String> jsonMap = Server.requestJson(exchange);
                Server.deleteUser(operation.id);
                Server.response(exchange, 200, "Received POST request at /user/delete/"+operation.id+" to delete user");
            } else {
                // Send a response 
                Server.response(exchange,400,"Received POST request at /user with invalid format");
            }
        } else if ("GET".equals(requestMethod)) {
            // ! Tengo que hacer un switch pero me da pereza
            if(operation.action==UrlAction.USER){
                // Get user
                String user = Server.userToJson(Server.getUser(operation.id));
                Server.response(exchange, 200, user);
            }else if(operation.action==UrlAction.STUDENT){
                // Get student
                String user = Server.userToJson(Server.getStudent(operation.id));
                Server.response(exchange, 200, user);
            }else if(operation.action==UrlAction.TEACHER){
                // Get teacher
                String user = Server.userToJson(Server.getUser(operation.id));
                Server.response(exchange, 200, user);
            }else if(operation.action==UrlAction.ALL_USERS){
                // Get all users
                String allUsers = Server.multipleUsersToJson(Server.getAllUsers(-1));
                Server.response(exchange, 200, allUsers);
            }else if(operation.action==UrlAction.ALL_STUDENTS){
                // Get all students
                String allUsers = Server.multipleUsersToJson(Server.getAllStudents());
                Server.response(exchange, 200, allUsers);
            }else if(operation.action==UrlAction.ALL_TEACHERS){
                // Get all teachers
                String allUsers = Server.multipleUsersToJson(Server.getAllUsers(0));
                Server.response(exchange, 200, allUsers);
            }
            else{
                // Send a response 
               Server.response(exchange,400,"Received GET request at /user with invalid format");
            }
            
        } else {
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

            if(size==2){ 
                if(idString.equals("user")){
                    operation.set(0,UrlAction.ALL_USERS);//All users
                } 
            }
            else if(size==3){
                if(parts[1].equals("user")){
                    if(idString.equals("new")) operation.set(0,UrlAction.NEW_USER);//New user
                    else if(parts[2].equals("student")) operation.set(0,UrlAction.ALL_STUDENTS);
                    else if(parts[2].equals("teacher")) operation.set(0,UrlAction.ALL_TEACHERS);
                    else operation.set(Integer.parseInt(idString),UrlAction.USER);// A user
                }
            }
            else if(size==4){
                if(parts[1].equals("user")){
                    if(parts[2].equals("student")){
                        operation.set(Integer.parseInt(idString),UrlAction.STUDENT);
                    }
                    else if(parts[2].equals("teacher")){
                        operation.set(Integer.parseInt(idString),UrlAction.TEACHER);
                    }
                    else if(parts[2].equals("delete")){
                        operation.set(Integer.parseInt(idString),UrlAction.DELETE_USER);
                    }
                }
            }

        }catch(NumberFormatException e){
            return operation; //Invalid URL format
        }
        return operation;
    }
}