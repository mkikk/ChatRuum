package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class CryptoTest {
    @Test
    public void testEncrypt() {
        String password = "1234";
        byte[] key = Crypto.generateKey("1234");
        byte[] iv = Crypto.generateIV();
        byte[] encrypted1 = Crypto.encrypt(password, key, iv);
        byte[] encrypted2 = Crypto.encrypt(password, key, iv);
        System.out.println("1. " + password + " --> " + Arrays.toString(encrypted1));
        System.out.println("1. " + password + " --> " + Arrays.toString(encrypted2));
        assertArrayEquals(encrypted1, encrypted2);

    }
}
