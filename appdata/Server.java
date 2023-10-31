import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.InetSocketAddress;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Server {

    private static Connection connection = null;

	public static void queryTest(){
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM students");

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
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
		}

	}

    public static void main(String[] args) throws IOException {
        // Create a SQL connection
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:db/appcesible.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        queryTest();

        // Create an HTTP server that listens on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Create a context for the root path "/"
        server.createContext("/", new MyHandler());

        // Start the server
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get the request method (POST, GET, etc.)
            String requestMethod = exchange.getRequestMethod();

            if ("POST".equals(requestMethod)) {
                // Read the request body
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }

                // Parse the JSON payload manually
                Map<String, String> jsonMap = parseJson(requestBody.toString());

                // Get the "name" field from the JSON payload
                String name = jsonMap.get("name");

                // Send a response with the extracted "name" value
                String response = "Received name: " + name + "\n";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                // Handle other HTTP methods or provide an error response
                String response = "Unsupported HTTP method";
                exchange.sendResponseHeaders(405, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private Map<String, String> parseJson(String json) {
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
    }
}
