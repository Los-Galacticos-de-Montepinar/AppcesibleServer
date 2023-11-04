public class UrlOperation{
    public int id;
    public UrlAction action;
    public UrlOperation(int id,UrlAction action){
        this.id = id;
        this.action = action;
    }
    public void set(int id,UrlAction action){
        this.id = id;
        this.action = action;            
    }
}
