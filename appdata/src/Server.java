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

    // Login
    public static String authenticate(String inputName,String inputPassword){
        System.out.println("authenticating...");
        String token = "";
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM user cross join loginInfo WHERE user.id = loginInfo.idUser AND userName=?");
            statement.setString(1, inputName);
            ResultSet resultSet = statement.executeQuery();
            int id = resultSet.getInt("id");
            String password = resultSet.getString("textPass");
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

    public static String authenticate(String inputName,String inputPass0,String inputPass1,String inputPass2){
        System.out.println("authenticating...");
        String token = "";
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM user cross join loginInfo WHERE user.id = loginInfo.idUser AND userName=?");
            statement.setString(1, inputName);
            ResultSet resultSet = statement.executeQuery();
            int id = resultSet.getInt("id");
            String passPart0 = resultSet.getString("passPart0");
            String passPart1 = resultSet.getString("passPart1");
            String passPart2 = resultSet.getString("passPart2");

            int passCount = 0;

            if(inputPass0==null){
                token = "0";
            }else if(inputPass0.equals(passPart0)){
                passCount++;
            }else{
                return "";
            }

            if(inputPass1==null){
                token = "0";
            }else if(inputPass1.equals(passPart1)){
                passCount++;
            }else{
                return "";
            }

            if(inputPass2==null){
                token = "0";
            }else if(inputPass2.equals(passPart2)){
                passCount++;
            }else{
                return "";
            }

            if(passCount==3){
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

    public static int getAuthenticationMethod(String inputName){
        int method = -1;
        try {
            PreparedStatement existsStatement = connection.prepareStatement("SELECT COUNT(*) FROM user WHERE userName=\'"+inputName+"\'");
            ResultSet exists = existsStatement.executeQuery();
            if(exists.getInt(1)==1){
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM user cross join loginInfo WHERE user.id = loginInfo.idUser AND userName=?");
                statement.setString(1, inputName);
                ResultSet resultSet = statement.executeQuery();

                method = resultSet.getInt("method");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return method;
    }

    // Create task in the BD
    public static int createTask(String title,String desc){
        System.out.println("creating task...");
        try{
            PreparedStatement statement = connection.prepareStatement("INSERT INTO task (id,title, taskDesc) VALUES (NULL,?,?);");
            statement.setString(1,title);
            statement.setString(2,desc);
            statement.executeUpdate();

            PreparedStatement lastTaskStatement = connection.prepareStatement("SELECT MAX(id) FROM task;");
            ResultSet resultSet = lastTaskStatement.executeQuery();
            int id = resultSet.getInt(1);
            
            return id;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    // Create task step in the BD
    public static void createTaskStep(int taskId,String desc, int order,String stepMedia){
        System.out.println("creating step...");
        try{
            PreparedStatement statement = connection.prepareStatement("INSERT INTO taskStep (id, stepDesc,stepMedia,taskOrder,idTask) VALUES (NULL,?,?,?,?);");
            statement.setString(1,desc);
            statement.setString(2,stepMedia);
            statement.setInt(3,order);
            statement.setInt(4,taskId);
            statement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    // Get task from the BD
    public static ResultSet getTask(int id){
        System.out.println("getting task...");
        ResultSet resultSet = null;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM task WHERE id=?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Get taskstep from the BD
    public static ResultSet getTaskStep(int id){
        System.out.println("getting taskstep...");
        ResultSet resultSet = null;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM taskStep WHERE id=?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Get all task from BD
    public static ResultSet getAllTasks(){
        System.out.println("getting all tasks...");
        ResultSet resultSet = null;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM task");
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Get all task steps that make up a task from BD
    public static ResultSet getTaskStepsFromTask(int id){
        System.out.println("getting tasks steps from task...");
        ResultSet resultSet = null;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM taskStep WHERE idTask=?;");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Update user in the BD
    public static void updateUser(int id,String userName,int idPFP,int idClass,int letterSize,int interactionFormat){
        System.out.println("updating user...");
        // * Esto es muy feo pero no se como hacerlo mas bonito
        try {
            if(userName!=null){
                PreparedStatement statement = connection.prepareStatement("UPDATE user SET username=? WHERE id=?;");
                statement.setString(1, userName);
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

    // Update user login info
    public static void updateUserLogin(int id,int method, String textPass, String passPart0, String passPart1, String passPart2){
        try {
            PreparedStatement methodStatement = connection.prepareStatement("UPDATE loginInfo SET method=? WHERE idUser=?;");
            methodStatement.setInt(1, method);
            methodStatement.setInt(2, id);
            methodStatement.executeUpdate();

            if(textPass!=null){
                PreparedStatement statement = connection.prepareStatement("UPDATE loginInfo SET textPass=? WHERE idUser=?;");
                statement.setString(1, textPass);
                statement.setInt(2, id);
                statement.executeUpdate();
            }
            if(passPart0!=null){
                PreparedStatement statement = connection.prepareStatement("UPDATE loginInfo SET passPart0=? WHERE idUser=?;");
                statement.setString(1, passPart0);
                statement.setInt(2, id);
                statement.executeUpdate();
            }
            if(passPart1!=null){
                PreparedStatement statement = connection.prepareStatement("UPDATE loginInfo SET passPart1=? WHERE idUser=?;");
                statement.setString(1, passPart1);
                statement.setInt(2, id);
                statement.executeUpdate();
            }
            if(passPart2!=null){
                PreparedStatement statement = connection.prepareStatement("UPDATE loginInfo SET passPart2=? WHERE idUser=?;");
                statement.setString(1, passPart2);
                statement.setInt(2, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get All users from the BD
    public static ResultSet getAllUsers(int type){
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
    public static ResultSet getAllStudents(){
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
    public static ResultSet getUser(int id){
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
    public static ResultSet getStudent(int id){
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
    public static void deleteUser(int id){
        System.out.println("deleting user...");
        try{
            PreparedStatement statement = connection.prepareStatement("DELETE FROM user WHERE id=?");
            statement.setInt(1,id);
            statement.executeUpdate();
            PreparedStatement studentStatement = connection.prepareStatement("DELETE FROM student WHERE idUser=?");
            studentStatement.setInt(1,id);
            studentStatement.executeUpdate();
            PreparedStatement loginStatement = connection.prepareStatement("DELETE FROM loginInfo WHERE idUser=?");
            loginStatement.setInt(1,id);
            loginStatement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    // Create user in the BD
    public static int createUser(String username, int idClass, int userType,int pfp){
        System.out.println("creating user...");
        int id = -1;
        try{
            PreparedStatement statement = connection.prepareStatement("INSERT INTO user (id,userName,idProfileImg,userType,idClass) VALUES (NULL,?,?,?,?);");
            statement.setString(1, username);
            statement.setInt(2, pfp);
            statement.setInt(3, userType);
            statement.setInt(4, idClass);   
            statement.executeUpdate();

            PreparedStatement lastUserStatement = connection.prepareStatement("SELECT MAX(id) FROM user;");
            ResultSet resultSet = lastUserStatement.executeQuery();
            id = resultSet.getInt(1);

        }catch(SQLException e){
            e.printStackTrace();
        }
        return id;
    }

    // Create student in the BD
    public static void createStudent(int userId,int letterSize,int interactionFormat){
        System.out.println("Creating student...");
        try{
            PreparedStatement statement = connection.prepareStatement("INSERT INTO student (id,idUSer,letterSize,interactionFormat) VALUES (NULL,?,?,?);");
            statement.setInt(1, userId);
            statement.setInt(2, letterSize);
            statement.setInt(3, interactionFormat);

            statement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    // Create loginInfo in the BD
    public static void createLoginInfo(int userId,int method,String textPass,String passPart0,String passPart1,String passPart2){
        System.out.println("Creating loginInfo...");
        try{
            PreparedStatement statement = connection.prepareStatement("INSERT INTO loginInfo (id,idUser,method,textPass,passPart0,passPart1,passPart2) VALUES (NULL,?,?,?,?,?,?);");
            statement.setInt(1, userId);
            statement.setInt(2, method);
            statement.setString(3, textPass);
            statement.setString(4, passPart0);
            statement.setString(5, passPart1);
            statement.setString(6, passPart2);

            statement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static Map<String, String> parseJson(String json) {
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

    public static String userToJson(ResultSet resultSet){
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
                // TODO devolver el tipo de login
                // int loginType = resultSet.getInt("loginType");
                int interactionFormat = resultSet.getInt("interactionFormat");
                jsonResults += ",\"letterSize\":"+ letterSize + ",\"interactionFormat:\""+interactionFormat;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        jsonResults = "{" + jsonResults + "}";

        return jsonResults;
    }

    public static String multipleUsersToJson(ResultSet resultSet){
        String jsonResults = "";
        ArrayList<String> userList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                userList.add(userToJson(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonResults = "[" + String.join(",", userList) + "]";
        // Combine the JSON objects into an array
        return jsonResults;
    }

    public static String multipleTaskStepsToJson(ResultSet resultSet){
        String jsonResults = "";
        ArrayList<String> stepList = new ArrayList<>();
        try{
            while (resultSet.next()) {
                stepList.add(taskStepToJson(resultSet));
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonResults = "[" + String.join(",", stepList) + "]";
        return jsonResults;
    }

    public static String taskStepToJson(ResultSet resultSet){
        String jsonResults = "";
        try{
            int id = resultSet.getInt("id");
            String desc = resultSet.getString("stepDesc");
            String media = resultSet.getString("stepMedia");
            int order = resultSet.getInt("taskOrder");
            int idTask = resultSet.getInt("idTask");
         
            jsonResults = "{\"id\":" + id + ",\"desc\":\"" + desc + "\",\"media\":\"" + media +  "\",\"order\":"+order+",\"taskId\":"+idTask+"}";
        }catch(Exception e){
            e.printStackTrace();
        }

        return jsonResults;
    }

    public static String taskToJson(ResultSet resultSet){
        String jsonResults = "";
        try{
            int id = resultSet.getInt("id");
            String desc = resultSet.getString("taskDesc");
            String title = resultSet.getString("title");
         
            jsonResults = "{\"id\":" + id + ",\"desc\":\"" + desc + "\",\"title\":\"" + title +  "\"}";
        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonResults;
    }

    public static String multipleTasksToJson(ResultSet resultSet){
        String jsonResults = "";
        ArrayList<String> taskList = new ArrayList<>();
        try{
            while (resultSet.next()) {
                taskList.add(taskToJson(resultSet));
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonResults = "[" + String.join(",", taskList) + "]";
        return jsonResults;
    }

    public static Map<String, String> requestJson(HttpExchange exchange){
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

    // string to id
    public static int string2id(String string){
        try{
            return Integer.parseInt(string);
        } catch (Exception e) {
            return -1;
        }
    }

    public static void response(HttpExchange exchange, int code, String response){
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
        server.createContext("/task", new TaskHandler());

        // Start the server
        server.start();

        //! He eliminado el método encargado de cerrar la conexion con la bd, no se donde sería necesario ponerlo ahora
    }
}
