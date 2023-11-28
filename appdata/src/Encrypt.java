import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.*;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

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
            byte[] encryptedBytes = encryptCipher.doFinal(dataBytes);
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decrypt(byte[] data){
        genKeys();
        try {
            Cipher decryCipher = Cipher.getInstance("RSA");
            decryCipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
            byte[] decryptedData = decryCipher.doFinal(data);
            return new String(decryptedData,StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("HERE\n"+(new String(data,StandardCharsets.UTF_8)));
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

    public static PublicKey publicKeyFromPEMOld(String pem) throws Exception{
        String publicKeyPem = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replaceAll(System.lineSeparator(), "")
            .replace("-----END PUBLIC KEY-----","");
        
        // byte[] encoded = Base64.getDecoder().decode(publicKeyPem.getBytes());
        byte[] encoded = Base64.getMimeDecoder().decode(pem);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public static PublicKey publicKeyFromPEMOld2(String pem) throws Exception{
        KeyFactory factory = KeyFactory.getInstance("RSA");

        StringReader reader = new StringReader("Hello");
        char[] array = new char[100];
        reader.read(array);
        System.out.println(array);
        PemReader pemReader = new PemReader(reader) ;

        PemObject pemObject = pemReader.readPemObject();
        byte[] content = pemObject.getContent();
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
        return (RSAPublicKey) factory.generatePublic(pubKeySpec);   
    }

    public static RSAPublicKey publicKeyFromPEM(String data) throws IOException {
        String cleanedString = data.replace("\\n", "\n");
        System.out.println(cleanedString);
        StringReader keyReader = new StringReader(cleanedString);
        PEMParser pemParser = new PEMParser(keyReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

        Object parsedObject = pemParser.readObject();
        
        if (parsedObject instanceof SubjectPublicKeyInfo) {
            SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo) parsedObject;
            return (RSAPublicKey) converter.getPublicKey(publicKeyInfo);
        } else {
            throw new IllegalArgumentException("Invalid PEM format or missing public key info.");
        }
    }
}