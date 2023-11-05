import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.InetSocketAddress;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Server {

    private static Connection connection = null;

    // string to id
    public static int string2id(String string){
        try{
            return Integer.parseInt(string);
        } catch (Exception e) {
            return -1;
        }
    }

    // Session
    public static String authenticate(String inputName,String inputPassword){
        System.out.println("authenticating...");
        String token = "";
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE userName=?");
            statement.setString(1, inputName);
            ResultSet resultSet = statement.executeQuery();
            int id = resultSet.getInt("id");
            String password = resultSet.getString("passwd");
            if(inputPassword.equals(password)){
                token = SessionManager.findSessionTokenByUser(id);
                if(token == null){
                    token = SessionManager.createSessionToken(id);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;
    }

    // Update user in the BD
    private static void updateUser(int id,String userName,String passwd,int idPFP,int idClass,int letterSize,int loginType,int interactionFormat){
        System.out.println("updating user...");
        try {
            if(userName!=null){
                PreparedStatement statement = connection.prepareStatement("UPDATE user SET username=? WHERE id=?;");
                statement.setString(1, userName);
                statement.setInt(2, id);
                statement.executeUpdate();
            }
            if(passwd!=null){
                PreparedStatement statement = connection.prepareStatement("UPDATE user SET passwd=? WHERE id=?;");
                statement.setString(1, passwd);
                statement.setInt(2, id);
                statement.executeUpdate();
            }
            if(idPFP>=0){
                PreparedStatement statement = connection.prepareStatement("UPDATE user SET idProfileImg=? WHERE id=?;");
                statement.setInt(1, idPFP);
                statement.setInt(2, id);
                statement.executeUpdate();
            }
            if(idClass>=0){
                PreparedStatement statement = connection.prepareStatement("UPDATE user SET idClass=? WHERE id=?;");
                statement.setInt(1, idClass);
                statement.setInt(2, id);
                statement.executeUpdate();
            }
            if(letterSize>=0){
                PreparedStatement statement = connection.prepareStatement("UPDATE student SET letterSize=? WHERE idUser=?;");
                statement.setInt(1, letterSize);
                statement.setInt(2, id);
                statement.executeUpdate();
            }
            if(loginType>=0){
                PreparedStatement statement = connection.prepareStatement("UPDATE student SET loginType=? WHERE idUser=?;");
                statement.setInt(1, loginType);
                statement.setInt(2, id);
                statement.executeUpdate();
            }
            if(interactionFormat>=0){
                PreparedStatement statement = connection.prepareStatement("UPDATE student SET interactionFormat=? WHERE idUser=?;");
                statement.setInt(1, interactionFormat);
                statement.setInt(2, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get All users from the BD
    private static ResultSet getAllUsers(int type){
        System.out.println("getting all users...");
        ResultSet resultSet = null;
        PreparedStatement statement;
        try {
            if(type <= 0) statement = connection.prepareStatement("SELECT * FROM user");
            else{
                statement = connection.prepareStatement("SELECT * FROM user WHERE userType=?");
                statement.setInt(1, type);
            }
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    // Get All students from the BD
    private static ResultSet getAllStudents(){
        System.out.println("getting all students...");
        ResultSet resultSet = null;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM user cross join student WHERE user.id = student.idUser");
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    // Get User from the BD
    private static ResultSet getUser(int id){
        System.out.println("getting user...");
        ResultSet resultSet = null;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM user WHERE id=?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Get Studnet from the BD
    private static ResultSet getStudent(int id){
        System.out.println("getting student...");
        ResultSet resultSet = null;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM user cross join student WHERE user.id = student.idUser AND user.id = ?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Delete user in the BD
    private static void deleteUser(int id){
        System.out.println("deleting user...");
        try{
            PreparedStatement statement = connection.prepareStatement("DELETE FROM user WHERE id=?");
            statement.setInt(1,id);
            statement.executeUpdate();
            PreparedStatement studentStatement = connection.prepareStatement("DELETE FROM student WHERE idUser=?");
            studentStatement.setInt(1,id);
            studentStatement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    // Create user in the BD
    private static int createUser(String username,String passwd, int idClass, int userType,int pfp){
        System.out.println("creating user...");
        try{
            PreparedStatement statement = connection.prepareStatement("INSERT INTO user (id,userName, passwd,idProfileImg,userType,idClass) VALUES (NULL,?,?,?,?,?);");
            statement.setString(1, username);
            statement.setString(2, passwd);
            statement.setInt(3, pfp);
            statement.setInt(4, userType);
            statement.setInt(5, idClass);   
            statement.executeUpdate();

            if(userType == 1){
                
                PreparedStatement lastUserStatement = connection.prepareStatement("SELECT MAX(id) FROM user;");
                ResultSet resultSet = lastUserStatement.executeQuery();
                int id = resultSet.getInt("id");
                return id;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    // Create student in the BD
    private static void createStudent(int userId,int letterSize,int loginType,int interactionFormat){
        System.out.println("Creating student...");
        try{
            PreparedStatement statement = connection.prepareStatement("INSERT INTO student (id,userId,letterSize,loginType,interactionFormat) VALUES (NULL,?,?,?,?);");
            statement.setInt(1, userId);
            statement.setInt(2, letterSize);
            statement.setInt(3, loginType);
            statement.setInt(4, interactionFormat);

            statement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static Map<String, String> parseJson(String json) {
        Map<String, String> jsonMap = new HashMap<>();
        String[] keyValuePairs = json.replaceAll("[{}\"]", "").split(",");
        for (String pair : keyValuePairs) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                jsonMap.put(parts[0].trim(), parts[1].trim());
            }
        }
        return jsonMap;
    }

    private static String userToJson(ResultSet resultSet){
        // Convert the ResultSet to a list of JSON objects
        String jsonResults = "";
        try {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("userName");
            int pfp = resultSet.getInt("idProfileImg");
            int userType = resultSet.getInt("userType");
            int idClass = resultSet.getInt("idClass");
         
            jsonResults = "\"id\":" + id + ",\"userName\":\"" + name + "\",\"pfp\":" + pfp +  ",\"userType\":" + userType +  ",\"idClass\":" + idClass;

            if(userType == 1){
                int letterSize = resultSet.getInt("letterSize");
                int loginType = resultSet.getInt("loginType");
                int interactionFormat = resultSet.getInt("interactionFormat");
                jsonResults += ",\"letterSize\":"+ letterSize + ",\"loginType\":"+loginType+",\"interactionFormat:\""+interactionFormat;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        jsonResults = "{" + jsonResults + "}";

        return jsonResults;
    }

    private static String multipleUsersToJson(ResultSet resultSet){
        String jsonResults = "";
        ArrayList<String> userList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                userList.add(userToJson(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        jsonResults = "[" + String.join(",", userList) + "]";
        // Combine the JSON objects into an array
        return jsonResults;
    }

    private static Map<String, String> requestJson(HttpExchange exchange){
        // Read the request body
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return parseJson(requestBody.toString());
    }

    private static void response(HttpExchange exchange, int code, String response){
        try {
            exchange.sendResponseHeaders(code, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // Create a SQL connection
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:../db/appcesible.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create an HTTP server that listens on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Create a context for the user path
        server.createContext("/user", new UserHandler());
        server.createContext("/session", new SessionHandler());

        // Start the server
        server.start();

        //! He eliminado el método encargado de cerrar la conexion con la bd, no se donde sería necesario ponerlo ahora
    }

    static class SessionHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get the request method (POST, GET, etc.)
            String requestMethod = exchange.getRequestMethod();

            UrlOperation operation = analizeUrl(exchange.getRequestURI().getPath());
            Map<String, String> jsonMap = requestJson(exchange);
            if ("POST".equals(requestMethod)) {
                if(operation.action == UrlAction.LOGIN){
                    String name = jsonMap.get("userName");
                    String password = jsonMap.get("passwd");
                    String token = authenticate(name,password);
                    if(!token.equals("")){
                        response(exchange, 200, token);
                    }else{
                        response(exchange, 401, "Incorrect username or password");
                    }
                }else if(operation.action == UrlAction.LOGOUT){
                    String token = jsonMap.get("sessionToken");
                    if(SessionManager.invalidateSessionToken(token)){
                        response(exchange, 200, "Logout");
                    }else{
                        response(exchange, 401, "Incorrect token");
                    }
                } else{
                    // Send a response 
                    response(exchange,400,"Received POST request at /session with invalid format");
                }
            }else {
                // Handle other HTTP methods or provide an error response
                response(exchange,405,"Unsupported HTTP method");
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

    static class UserHandler implements HttpHandler {
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
                    Map<String, String> jsonMap = requestJson(exchange);

                    if(jsonMap!=null){
                        // Get fields from the JSON payload
                        String name = jsonMap.get("userName");
                        String passwd = jsonMap.get("passwd");
                        int idPFP = string2id(jsonMap.get("pfp"));
                        int idClass = string2id(jsonMap.get("idClass"));
                        
                        int letterSize = string2id(jsonMap.get("letterSize"));
                        int loginType = string2id(jsonMap.get("loginType"));
                        int interactionFormat = string2id(jsonMap.get("interactionFormat"));

                        updateUser(operation.id, name, passwd, idPFP,idClass,letterSize,loginType,interactionFormat);

                        // Send a response 
                        response(exchange, 200, "Received POST request at /user/"+operation.id+ " to update user");
                    } else {
                        response(exchange, 400, "Received POST request with invalid format");
                    }

                } else if(operation.action == UrlAction.NEW_USER){
                    // Add new user

                    // Parse the JSON payload manually
                    Map<String, String> jsonMap = requestJson(exchange);

                    // Get the fields from the JSON payload
                    String name = jsonMap.get("userName");
                    int pfp = Integer.parseInt(jsonMap.get("pfp"));
                    int idClass = Integer.parseInt(jsonMap.get("idClass"));
                    String pass = jsonMap.get("passwd");
                    int type = Integer.parseInt(jsonMap.get("userType"));

                    int studentUserId = createUser(name, pass, idClass, type, pfp);
                    if(studentUserId>=0){
                        int letterSize = Integer.parseInt(jsonMap.get("letterSize"));
                        int loginType = Integer.parseInt(jsonMap.get("loginType"));
                        int interactionFormat = Integer.parseInt(jsonMap.get("interactionFormat"));
                        createStudent(studentUserId,letterSize , loginType, interactionFormat);
                    }

                    // Send a response 
                    response(exchange, 200, "Received POST request at /user/new to create new user");
                } else if (operation.action == UrlAction.DELETE_USER){
                    // Parse the JSON payload manually
                    Map<String, String> jsonMap = requestJson(exchange);
                    deleteUser(operation.id);
                    response(exchange, 200, "Received POST request at /user/delete/"+operation.id+" to delete user");
                } else {
                    // Send a response 
                    response(exchange,400,"Received POST request at /user with invalid format");
                }
            } else if ("GET".equals(requestMethod)) {
                // ! Tengo que hacer un switch pero me da pereza
                if(operation.action==UrlAction.USER){
                    // Get user
                    String user = userToJson(getUser(operation.id));
                    response(exchange, 200, user);
                }else if(operation.action==UrlAction.STUDENT){
                    // Get student
                    String user = userToJson(getStudent(operation.id));
                    response(exchange, 200, user);
                }else if(operation.action==UrlAction.TEACHER){
                    // Get teacher
                    String user = userToJson(getUser(operation.id));
                    response(exchange, 200, user);
                }else if(operation.action==UrlAction.ALL_USERS){
                    // Get all users
                    String allUsers = multipleUsersToJson(getAllUsers(-1));
                    response(exchange, 200, allUsers);
                }else if(operation.action==UrlAction.ALL_STUDENTS){
                    // Get all students
                    String allUsers = multipleUsersToJson(getAllStudents());
                    response(exchange, 200, allUsers);
                }else if(operation.action==UrlAction.ALL_TEACHERS){
                    // Get all teachers
                    String allUsers = multipleUsersToJson(getAllUsers(0));
                    response(exchange, 200, allUsers);
                }
                else{
                    // Send a response 
                    response(exchange,400,"Received GET request at /user with invalid format");
                }
                
            } else {
                // Handle other HTTP methods or provide an error response
                response(exchange,405,"Unsupported HTTP method");
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
}
