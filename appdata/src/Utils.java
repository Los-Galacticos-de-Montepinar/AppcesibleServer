import java.util.ArrayList;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


public class Utils {

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

    public static String userToJson(ResultSet resultSet){
        // Convert the ResultSet to a list of JSON objects
        String jsonResults = "";
        try {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("userName");
            int pfp = resultSet.getInt("idProfileImg");
            int userType = resultSet.getInt("userType");
            int idClass = resultSet.getInt("idClass");
            int loginType = resultSet.getInt("method");
         
            jsonResults = "\"id\":" + id + ",\"userName\":\"" + name + "\",\"pfp\":" + pfp +  ",\"userType\":" + userType +  ",\"idClass\":" + idClass + ",\"loginType\":" + loginType;

            if(userType == 1 && hasColumn(resultSet, "letterSize")){
                int letterSize = resultSet.getInt("letterSize");
                // int loginType = resultSet.getInt("loginType");
                int interactionFormat = resultSet.getInt("interactionFormat");
                jsonResults += ",\"letterSize\":"+ letterSize + ",\"interactionFormat\":"+interactionFormat;
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

        public static String itemToJson(ResultSet resultSet){
        String jsonResult = "";
        try{
            int id = resultSet.getInt("id");
            String name = resultSet.getString("itemName");
            String image = resultSet.getString("imageName");
            int count = resultSet.getInt("count");
         
            jsonResult = "{\"id\":" + id + ",\"name\":\"" + name + "\",\"image\":" + image + ",\"count\":" + count + "}";
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

    public static String taskItemToJson(ResultSet resultSet){
        String jsonResult = "";
        try{
            int id = resultSet.getInt("id");
            String name = resultSet.getString("itemName");
            String image = resultSet.getString("imageName");
            String idTask = resultSet.getString("idTask");
            String quantity = resultSet.getString("quantity");
            String idItem = resultSet.getString("idItem");
         
            jsonResult = "{\"id\":" + id + ",\"name\":\"" + name + "\",\"image\":" + image + ",\"idTask\":" + idTask + ",\"count\":" + quantity +",\"item\":" + idItem +"}";
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

    public static String assigmentToJson(ResultSet resultSet){
        String jsonResult = "";
        try{
            int id = resultSet.getInt("id");
            int idUser = resultSet.getInt("idUser");
            int idTask = resultSet.getInt("idTask");
            String date = resultSet.getString("finishDate");
         
            jsonResult = "{\"id\":" + id + ",\"idUser\":" + idUser + ",\"idTask\":" + idTask + ",\"date\":\"" + date +  "\"}";
        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonResult;
    }

    public static String multipleAssignmentsToJson(ResultSet resultSet){
        String jsonResult = "";
        ArrayList<String> list = new ArrayList<>();
        try{
            while (resultSet.next()) {
                list.add(assigmentToJson(resultSet));
            }
         
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonResult = "[" + String.join(",", list) + "]";
        return jsonResult;
    }

    public static String mediaMetadataToJson(ResultSet resultSet){
        String jsonResult = "";
        try{
            int id = resultSet.getInt("id");
            int type = resultSet.getInt("imageType");
            String desc = resultSet.getString("imageDesc");
         
            jsonResult = "{\"id\":" + id + ",\"imageType\":" + type + ",\"imageDesc\":\"" + desc + "\"}";
        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonResult;
    }

    public static String multipleMediaMetadataToJson(ResultSet resultSet){
        String jsonResult = "";
        ArrayList<String> list = new ArrayList<>();
        try{
            while (resultSet.next()) {
                list.add(mediaMetadataToJson(resultSet));
            }
         
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonResult = "[" + String.join(",", list) + "]";
        return jsonResult;
    }

    public static String classroomToJson(ResultSet resultSet){
        String jsonResult = "";
        try{
            int id = resultSet.getInt("id");
            String name = resultSet.getString("className");
         
            jsonResult = "{\"id\":" + id +  ",\"className\":\"" + name + "\"}";
        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonResult;
    }

    public static String multipleClassroomsToJson(ResultSet resultSet){
        String jsonResult = "";
        ArrayList<String> list = new ArrayList<>();
        try{
            while (resultSet.next()) {
                list.add(classroomToJson(resultSet));
            }
         
        }catch(Exception e){
            e.printStackTrace();
        }
        jsonResult = "[" + String.join(",", list) + "]";
        return jsonResult;
    }
    
    public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equals(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<byte[]> splitByteArray(byte[] bytes,String string,int max){
        ArrayList<byte[]> list = new ArrayList<>();
        int currentStringIndex = 0;
        int initByteArrayIndex = 0;
        int nParts = 0;

        int i = 0;
        while(i < bytes.length){
            currentStringIndex = 0;
            while(
                i+currentStringIndex < bytes.length &&
                currentStringIndex < string.length() && 
                bytes[i+currentStringIndex] == string.charAt(currentStringIndex) && 
                nParts < max-1
            ){
                currentStringIndex++;
                if(currentStringIndex==string.length()){
                    int length = i-initByteArrayIndex;
                    i += currentStringIndex;

                    byte[] newByteArray = new byte[length];
                    System.arraycopy(bytes, initByteArrayIndex, newByteArray, 0, length);
                    list.add(newByteArray);
                    initByteArrayIndex = i;
                    nParts++;
                }
            }

            i++;
        }

        int length = bytes.length-initByteArrayIndex;

        byte[] newByteArray = new byte[length];
        System.arraycopy(bytes, initByteArrayIndex, newByteArray, 0, length);
        list.add(newByteArray);

        return list;
    }

    public static ArrayList<byte[]> splitByteArray(byte[] bytes,String string){
        return splitByteArray(bytes, string,Integer.MAX_VALUE);
    }

    public static int byteArrayIndexOf(byte[] bytes, char c){
        for(int i = 0 ; i < bytes.length; i++){
            if(bytes[i]==c) return i;
        }
        return -1;
    }

    public static byte[] byteArrayRemove(byte[] bytes, char c){
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        for(int i = 0 ; i < bytes.length; i++){
            if(bytes[i]!=c){
                buffer.put(bytes[i]);
            }
        }
        buffer.rewind();
        byte[] out = new byte[buffer.remaining()];
        buffer.get(out);
        return out;
    }

    public static byte[] byteArrayRemove(byte[] bytes, String string){
        ArrayList<byte[]> parts = Utils.splitByteArray(bytes, string);
        int size = 0;
        for(byte[] part : parts){
            size += part.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(size);
        for(byte[] part : parts){
            for(int i = 0; i < part.length; i++){
                buffer.put(part[i]);
            }
        }
        buffer.rewind();
        byte[] out = new byte[buffer.remaining()];
        buffer.get(out);
        return out;
    }

    public static byte[] byteArrayReplace(byte[] bytes, char c, char newChar){
        byte[] out = bytes;
        for(int i = 0 ; i < bytes.length; i++){
            if(bytes[i]==c){
                out[i] = (byte)newChar;
            }
        }
        return out;
    }

    public static byte[] subByteArray(byte[] bytes, int index){
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        for(int i = index ; i < bytes.length; i++){
            buffer.put(bytes[i]);
        }
        buffer.rewind();
        byte[] out = new byte[buffer.remaining()];
        buffer.get(out);
        return out;
    }

    public static void printBytes(byte[] input){
        int j = 0;
        System.out.print("->");
        while(j < input.length){
            System.out.print((char)input[j]);
            j++;
        }
        System.out.println("<- " + j);
    }
}
