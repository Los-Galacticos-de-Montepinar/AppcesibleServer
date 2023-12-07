import java.util.ArrayList;
import java.util.TreeMap;

public class MultipartSection {
    TreeMap<String,String> tags;
    String contentType = "";
    byte[] data;

    public MultipartSection(String input){
        tags = new TreeMap<String,String>();
        fromStringSection(input);
    }

    public MultipartSection(byte[] input){
        tags = new TreeMap<String,String>();
        fromBytesSection(input);
    }

    private void fromStringSection(String input){
        String[] parts = input.split("\n\n",2);
        generateTags(parts[0]);
        generateData(parts[1]);
    }

    private void fromBytesSection(byte[] input){
        ArrayList<byte[]> parts = Utils.splitByteArray(input, "\r\n\r\n",2);
        
        generateTags(parts.get(0));

        data = parts.get(1);
    }

    private void generateTags(byte[] tagBytes){
        String tagString = new String(tagBytes);
        generateTags(tagString);
    }

    private void generateTags(String tagString){
        String [] lines = tagString.split("\r\n");
        if(lines.length>2){
            int index = lines[2].indexOf(":");
            if(index>0){
                contentType = lines[2].substring(index).replace(" ", "");
            }else{
                contentType = "text/plain";
            }
            
        }else{
            contentType = "text/plain";
        }
        String [] parts = lines[1].split(";");
        for(int i = 1 ; i < parts.length; i++){
            String [] tagParts = parts[i].split("=",2);
            String value = tagParts[1].replace("\"", "");
            String key = tagParts[0].replace(" ", "");
            System.out.println("tags: "+key + " value: " + value);
            tags.put(key,value);
        }
    }

    private void generateData(String dataString){
        data = dataString.getBytes();
    }

    public String getTag(String key){
        return tags.get(key);
    }

    public byte[] getData(){
        return data;
    }

    public String getContentType(){
        return contentType;
    }
}