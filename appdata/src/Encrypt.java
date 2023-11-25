import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Encrypt {
    private static KeyPairGenerator generator;
    private static KeyPair pair = null;

    public static PublicKey genKeys(){
        if(pair==null){
            try {
                generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048);
                pair = generator.generateKeyPair();
                return pair.getPublic();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
        return pair.getPublic();
    }

    public static String encrypt(String data,PublicKey key){
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE,key);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedData = encryptCipher.doFinal(dataBytes);
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String data){
        genKeys();
        try {
            Cipher decryCipher = Cipher.getInstance("RSA");
            decryCipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] decryptedData;
            decryptedData = decryCipher.doFinal(dataBytes);
            return new String(decryptedData,StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static PublicKey getKeyFromString(String key){
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyBytes);
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStringFromKey(PublicKey key){
        byte[] keyBytes = key.getEncoded();
        return new String(keyBytes,StandardCharsets.UTF_8);
    }

    public static String getPublicString(){
        genKeys();
        return getStringFromKey(pair.getPublic());
    }
}
