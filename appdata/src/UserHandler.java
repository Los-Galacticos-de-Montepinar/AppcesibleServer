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
        if ("POST".equals(requestMethod)) {
            if(operation.action == UrlAction.USER){
                // Parse the JSON payload manually
                Map<String, String> jsonMap = Server.requestJson(exchange);

                if(jsonMap!=null){
                    // Get fields from the JSON payload
                    String name = jsonMap.get("userName");

                    int idPFP = Server.string2id(jsonMap.get("pfp"));
                    int idClass = Server.string2id(jsonMap.get("idClass"));
                    
                    int letterSize = Server.string2id(jsonMap.get("letterSize"));
                    int loginType = Server.string2id(jsonMap.get("loginType"));
                    int interactionFormat = Server.string2id(jsonMap.get("interactionFormat"));
                    Server.updateUser(operation.id, name,idPFP,idClass,letterSize,interactionFormat);
                    if(loginType >= 0){
                        String passwd = jsonMap.get("passwd");
                        int passPart0 = Server.string2id(jsonMap.get("passPart0"));
                        int passPart1 = Server.string2id(jsonMap.get("passPart1"));
                        int passPart2 = Server.string2id(jsonMap.get("passPart2"));
                        Server.updateUserLogin(operation.id, loginType, passwd,passPart0,passPart1,passPart2);
                    } 

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
               
                int type = Integer.parseInt(jsonMap.get("userType"));

                int userId = Server.createUser(name, idClass, type, pfp);
                if(type==1){
                    int letterSize = Server.string2id(jsonMap.get("letterSize"));
                    int interactionFormat = Server.string2id(jsonMap.get("interactionFormat"));
                    Server.createStudent(userId,letterSize, interactionFormat);
                }
                int loginType = Server.string2id(jsonMap.get("loginType"));
                String textPass = jsonMap.get("passwd");
                int passPart0 = Server.string2id(jsonMap.get("passPart0"));
                int passPart1 = Server.string2id(jsonMap.get("passPart1"));
                int passPart2 = Server.string2id(jsonMap.get("passPart2"));

                // Si no se especifica el tipo de login será de contraseña
                loginType = loginType == -1 ? 0 : loginType;
                Server.createLoginInfo(userId,loginType,textPass,passPart0,passPart1,passPart2);

                // Send a response 
                Server.response(exchange, 200, "Received POST request at /user/new to create new user");
            } else if (operation.action == UrlAction.DELETE_USER){
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
                String user = Utils.userToJson(Server.getUser(operation.id));
                Server.response(exchange, 200, user);
            }else if(operation.action==UrlAction.STUDENT){
                // Get student
                String user = Utils.userToJson(Server.getStudent(operation.id));
                Server.response(exchange, 200, user);
            }else if(operation.action==UrlAction.TEACHER){
                // Get teacher
                String user = Utils.userToJson(Server.getUser(operation.id));
                Server.response(exchange, 200, user);
            }else if(operation.action==UrlAction.ALL_USERS){
                // Get all users
                String allUsers = Utils.multipleUsersToJson(Server.getAllUsers(-1));
                Server.response(exchange, 200, allUsers);
            }else if(operation.action==UrlAction.ALL_STUDENTS){
                // Get all students
                String allUsers = Utils.multipleUsersToJson(Server.getAllStudents());
                Server.response(exchange, 200, allUsers);
            }else if(operation.action==UrlAction.ALL_TEACHERS){
                // Get all teachers
                String allUsers = Utils.multipleUsersToJson(Server.getAllUsers(0));
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

        int n = -2;

        if(Utils.compareURL(path, "/user")!=-2) operation.set(-1,UrlAction.ALL_USERS);
        n = Utils.compareURL(path, "/user/?"); if(n!=-2) operation.set(n,UrlAction.USER);
        if(Utils.compareURL(path, "/user/student")!=-2) operation.set(-1,UrlAction.ALL_STUDENTS);
        if(Utils.compareURL(path, "/user/teacher")!=-2) operation.set(-1,UrlAction.ALL_TEACHERS);
        n = Utils.compareURL(path, "/user/delete/?"); if(n!=-2) operation.set(n,UrlAction.DELETE_USER);
        if(Utils.compareURL(path, "/user/new")!=-2) operation.set(-1,UrlAction.NEW_USER);
        n = Utils.compareURL(path, "/user/student/?"); if(n!=-2) operation.set(n,UrlAction.STUDENT);
        n = Utils.compareURL(path, "/user/teacher/?"); if(n!=-2) operation.set(n,UrlAction.TEACHER);

        return operation;
    }
}