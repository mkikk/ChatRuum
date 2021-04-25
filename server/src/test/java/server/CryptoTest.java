package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class CryptoTest {
    @Test
    public void testEncrypt() {
        var password = new Password("1234");
        assertTrue(password.checkPassword("1234"));

        var password2 = new Password("pikktekst1234---------?=========LÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖ");
        assertTrue(password2.checkPassword("pikktekst1234---------?=========LÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖ"));
    }
}
