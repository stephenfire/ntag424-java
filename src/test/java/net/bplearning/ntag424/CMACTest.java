package net.bplearning.ntag424;

import net.bplearning.ntag424.aes.AESCMAC;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.macs.CMac;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Random;

public class CMACTest {

    public static byte[] randomKey(int length) {
        Random random = new Random();
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes); // Fills the byte array with random bytes
        return randomBytes;
    }

    private static byte[] calculate1(AESCMAC cmac, byte[] message) {
        return cmac.perform(message, 16);
    }

    private static byte[] calculate2(CMac cmac, byte[] message) {
        cmac.update(message, 0, message.length);
        byte[] result = new byte[cmac.getMacSize()];
        cmac.doFinal(result, 0);
        return result;
    }

    @Test
    public void testCMAC() throws IOException, GeneralSecurityException {
        byte[] keyBytes = randomKey(16);
        byte[] iv = randomKey(16);

        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, net.bplearning.ntag424.constants.Crypto.zeroIVPS);

        AESCMAC cmac1 = new AESCMAC(cipher, key);

        CMac cmac2 = new CMac(AESEngine.newInstance());
        CipherParameters keyParam = new KeyParameter(keyBytes);
        cmac2.init(keyParam);

        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            int msgLength = r.nextInt(128);
            byte[] msg = randomKey(msgLength);
            byte[] m1 = calculate1(cmac1, msg);
            byte[] m2 = calculate2(cmac2, msg);
            System.out.printf("msg: %s =>\nm1: %s\nm2: %s\n", Hex.encodeHexString(msg), Hex.encodeHexString(m1), Hex.encodeHexString(m2));
        }
    }
}
