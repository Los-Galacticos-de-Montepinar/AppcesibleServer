import java.util.ArrayList;
import java.sql.ResultSet;

public class Utils {

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

    static int compareURL(String in,String template){
        String[] inParts = in.split("/");
        String[] templateParts = template.split("/");

        if(inParts.length!=templateParts.length) return -2;
        int value = -1;
        for(int i = 1 ; i < inParts.length;i++){
            if(templateParts[i].equals("?")){
                value = Server.string2id(inParts[i]);
                if(value==-1) return -2;
            }else if(!templateParts[i].equals(inParts[i])) return -2;
        }

        return value;
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

    public static String taskToJson(ResultSet resultSet){
        String jsonResults = "";
        try{
            int id = resultSet.getInt("id");
            String desc = resultSet.getString("taskDesc");
            String title = resultSet.getString("title");
            int type = resultSet.getInt("taskType");
         
            jsonResults = "{\"id\":" + id + ",\"desc\":\"" + desc + "\",\"title\":\"" + title + "\",\"type\":\"" + type +  "\"}";
        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonResults;
    }

    public static String itemToJson(ResultSet resultSet){
        String jsonResult = "";
        try{
            int id = resultSet.getInt("id");
            String name = resultSet.getString("itemName");
            String image = resultSet.getString("imageName");
         
            jsonResult = "{\"id\":" + id + ",\"name\":\"" + name + "\",\"image\":\"" + image + "\"}";
        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonResult;
    }

    public static String multipleItemsToJson(ResultSet resultSet){
        String jsonResult = "";
        ArrayList<String> itemList = new ArrayList<>();
        try{
            while (resultSet.next()) {
                itemList.add(itemToJson(resultSet));
            }
         
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonResult = "[" + String.join(",", itemList) + "]";
        return jsonResult;
    }

    public static String taskItemToJson(ResultSet resultSet){
        String jsonResult = "";
        try{
            int id = resultSet.getInt("id");
            String name = resultSet.getString("itemName");
            String image = resultSet.getString("imageName");
            String idTask = resultSet.getString("idTask");
            String quantity = resultSet.getString("quantity");
            String idItem = resultSet.getString("idItem");
         
            jsonResult = "{\"id\":" + id + ",\"name\":\"" + name + "\",\"image\":\"" + image + "\",\"idTask\":" + idTask + ",\"count\":\"" + quantity +",\"item\":\"" + idItem +"}";
        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonResult;
    }

    public static String multipleTaskItemsToJson(ResultSet resultSet){
        String jsonResult = "";
        ArrayList<String> itemList = new ArrayList<>();
        try{
            while (resultSet.next()) {
                itemList.add(taskItemToJson(resultSet));
            }
         
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonResult = "[" + String.join(",", itemList) + "]";
        return jsonResult;
    }
}
