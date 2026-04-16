package security;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import model.Credential;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    
    // We derive a reliable AES key from the master password to encrypt/decrypt the vault
    private static SecretKeySpec generateKey(String masterPassword) throws Exception {
        byte[] key = masterPassword.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // Use only first 128 bit
        return new SecretKeySpec(key, ALGORITHM);
    }
    
    public static void encryptAndSave(List<Credential> vault, String masterPassword, File file) throws Exception {
        SecretKeySpec secretKey = generateKey(masterPassword);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        try (FileOutputStream fos = new FileOutputStream(file);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher);
             ObjectOutputStream oos = new ObjectOutputStream(cos)) {
            oos.writeObject(vault);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<Credential> decryptAndLoad(String masterPassword, File file) throws Exception {
        SecretKeySpec secretKey = generateKey(masterPassword);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        try (FileInputStream fis = new FileInputStream(file);
             CipherInputStream cis = new CipherInputStream(fis, cipher);
             ObjectInputStream ois = new ObjectInputStream(cis)) {
            return (List<Credential>) ois.readObject();
        }
    }
}
