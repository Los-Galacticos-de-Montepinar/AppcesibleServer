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

    public static byte[] encrypt(String data,PublicKey key){
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE,key);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            return encryptCipher.doFinal(dataBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decrypt(byte[] data){
        genKeys();
        try {
            Cipher decryCipher = Cipher.getInstance("RSA");
			System.out.println("decryCipher");
            decryCipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
			System.out.println("init");
            byte[] decryptedData = decryCipher.doFinal(data);
			System.out.println("decrypted");
            return new String(decryptedData,StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static PublicKey getKeyFromBytes(byte[] key){
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(key);
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getBytesFromKey(PublicKey key){
        return key.getEncoded();
    }

    public static byte[] getPublicBytes(){
        genKeys();
        return getBytesFromKey(pair.getPublic());
    }
}
