package net.micode.notes.tool;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY = "0123456789ABCDEF"; // 16字节的密钥
    private static final String IV = "0123456789ABCDEF"; // 16字节的初始向量

    public static String encrypt(String plainText) throws Exception {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV.getBytes()));
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }


    public static String decrypt(String encryptedText) throws Exception {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV.getBytes()));
        byte[] encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

}