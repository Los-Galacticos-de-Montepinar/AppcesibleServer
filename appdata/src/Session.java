import java.security.PublicKey;

public class Session {
    private int id = 0;
    private PublicKey publicKey;
    Session(int id,PublicKey key){
        this.id = id;
        publicKey = key;
    }

    public int getId(){
        return id;
    }

    public byte[] encrypt(String data){
        return Encrypt.encrypt(data, publicKey);
    }
}
