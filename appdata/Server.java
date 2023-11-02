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
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Server {

    private static Connection connection = null;

    //! Query de ejemplo, no usa prepared statements, evitar usar
	private static void queryTest(){
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM user");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("userName");
                int age = resultSet.getInt("age");

                System.out.println("ID: " + id + ", Name: " + name + ", Age: " + age);
            }
		} catch (Exception e) {
            e.printStackTrace();
		} finally {
    		// Close the ResultSet, Statement, and Connection
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
		}

	}

    // Update user in the BD
    private static void updateUser(int id,String userName,String passwd,int idPFP){
        System.out.println("updating user...");
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE user SET username=?, passwd=?,idProfileImg=? WHERE id=?;");
            statement.setString(1, userName);
            statement.setString(2, passwd);
            statement.setInt(3, idPFP);
            statement.setInt(4, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        System.err.println("query");
        return resultSet;
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
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("userName");
                String pfp = resultSet.getString("idProfileImg");
                String userType = resultSet.getString("userType");
                String idClass = resultSet.getString("idClass");                
                String age = resultSet.getString("age");

                jsonResults = "{\"id\":" + id + ",\"userName\":\"" + name + "\",\"pfp\":" + pfp +  ",\"userType\":" + userType +  ",\"idClass\":" + idClass + ",\"age\":"+ age + "}";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
            connection = DriverManager.getConnection("jdbc:sqlite:db/appcesible.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create an HTTP server that listens on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Create a context for the root path "/"
        server.createContext("/user/", new UserHandler());

        // Start the server
        server.start();

        //! He eliminado el método encargado de cerrar la conexion con la bd, no se donde sería necesario ponerlo ahora
    }

    static class UserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get the request method (POST, GET, etc.)
            String requestMethod = exchange.getRequestMethod();

            if ("POST".equals(requestMethod)) {

                int id = extractIdFromUrl(exchange.getRequestURI().getPath());
                if(id>=0){
                    // Parse the JSON payload manually
                    Map<String, String> jsonMap = requestJson(exchange);

                    if(jsonMap!=null){
                        // Get fields from the JSON payload
                        String name = jsonMap.get("name");
                        String passwd = jsonMap.get("passwd");
                        int idPFP = Integer.parseInt(jsonMap.get("pfp"));
                        updateUser(id, name, passwd, idPFP);

                        // Send a response 
                        response(exchange, 200, "Received POST request at /user/"+id+ " to update user");
                    } else {
                        response(exchange, 400, "Received POST request with invalid format");
                    }

                } else if(id==-2){
                    // Add new user
                    // TODO

                    // Parse the JSON payload manually
                    Map<String, String> jsonMap = requestJson(exchange);

                    // Get the fields from the JSON payload
                    String name = jsonMap.get("name");
                    String age = jsonMap.get("age");

                    // Send a response 
                    response(exchange, 200, "Received POST request at /user/new {name:"+name+", age:"+age+"}");
                } else {
                    // Send a response 
                    response(exchange,400,"Received POST request at /user/ with invalid format");
                }
            } else if ("GET".equals(requestMethod)) {
                // Send a response
                int id = extractIdFromUrl(exchange.getRequestURI().getPath());
                if(id>=0)
                {
                    String user = userToJson(getUser(id));
                    System.out.println(user);
                    response(exchange, 200, user);
                }else {
                    // Send a response 
                    response(exchange,400,"Received GET request at /user/ with invalid format");
                }
                
            } else {
                // Handle other HTTP methods or provide an error response
                response(exchange,405,"Unsupported HTTP method");
            }
        }

        //** Este método no es perfecto, lo unico que mira el es valor tras la última '/'
        private int extractIdFromUrl(String path){
            try {
                String idString = path.substring(path.lastIndexOf('/') + 1);
                if(idString.equals("new")) return -2;
                else return Integer.parseInt(idString);
            }catch(NumberFormatException e){
                return -1; //Invalid URL format
            }
        }
    }
}
