package server;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Crypto {
    public static byte[] encrypt(String raw) {
        try {
            byte[] key = generateKey(raw);
            byte[] iv = generateIV();
            byte[] data = raw.getBytes(StandardCharsets.UTF_8);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(key, "AES"),
                    new GCMParameterSpec(128, iv));
            return cipher.doFinal(data);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static byte[] encrypt(String raw, byte[] key, byte[] iv) {
        try {
            byte[] data = raw.getBytes(StandardCharsets.UTF_8);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(key, "AES"),
                    new GCMParameterSpec(128, iv));
            return cipher.doFinal(data);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static byte[] generateKey(String password) {
        int iterationCount = 1000;
        SecureRandom rng = new SecureRandom();
        byte[] salt = new byte[32];
        rng.nextBytes(salt);

        int outputLengthBits = 256;

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, outputLengthBits);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();

        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            System.out.println(e);
            return null;
        }
    }

    public static byte[] generateIV() {
        SecureRandom rng = new SecureRandom();
        byte[] iv = new byte[12];
        rng.nextBytes(iv);
        return iv;
    }
}
