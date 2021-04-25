package server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Password {
    private final byte[] password;
    private final byte[] key;
    private final byte[] iv;

    public Password(@NotNull String password) {
        try {
            this.key = generateKey(password);
            this.iv = generateIV();
            this.password = encrypt(password);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Password encrypting failed in constructor", e);
        }
    }

    public Password(
            @JsonProperty(value = "password") byte[] password,
            @JsonProperty(value = "key") byte[] key,
            @JsonProperty(value = "iv") byte[] iv
    ) {
        this.password = password;
        this.key = key;
        this.iv = iv;
    }

    public boolean checkPassword(@NotNull String givenPassword) {
        try {
            var checkPassword = encrypt(givenPassword);
            return Arrays.equals(password, checkPassword);
        } catch (GeneralSecurityException e) {
            System.err.println("Password encrypting failed while checking password '" + givenPassword + "'");
            e.printStackTrace();
            return false;
        }
    }

    private byte[] encrypt(String raw) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] data = raw.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(key, "AES"),
                new GCMParameterSpec(128, iv));
        return cipher.doFinal(data);
    }

    private static byte[] generateKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterationCount = 1000;
        SecureRandom rng = new SecureRandom();
        byte[] salt = new byte[32];
        rng.nextBytes(salt);

        int outputLengthBits = 256;

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, outputLengthBits);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
    }

    private static byte[] generateIV() {
        SecureRandom rng = new SecureRandom();
        byte[] iv = new byte[12];
        rng.nextBytes(iv);
        return iv;
    }
}
