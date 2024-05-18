package ru.stanley.Utils;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.util.encoders.Hex;

import java.security.SecureRandom;
import java.util.Arrays;

public class GOSTHashing {

    public static byte[] generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }

    public static byte[] computeHashWithSalt(String password, byte[] salt) {
        Digest digest = new GOST3411_2012_256Digest();
        byte[] passwordBytes = password.getBytes();
        byte[] combined = new byte[passwordBytes.length + salt.length];

        System.arraycopy(passwordBytes, 0, combined, 0, passwordBytes.length);
        System.arraycopy(salt, 0, combined, passwordBytes.length, salt.length);

        digest.update(combined, 0, combined.length);
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return hash;
    }

    public static String encodeSaltAndHash(byte[] salt, byte[] hash) {
        byte[] combined = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(hash, 0, combined, salt.length, hash.length);
        return Hex.toHexString(combined);
    }

    public static byte[][] decodeSaltAndHash(String encoded) {
        byte[] combined = Hex.decode(encoded);
        byte[] salt = Arrays.copyOfRange(combined, 0, 16);
        byte[] hash = Arrays.copyOfRange(combined, 16, combined.length);
        return new byte[][]{salt, hash};
    }

    public static boolean verifyPassword(String enteredSaltAndHash, String encodedSaltAndHash) {
        byte[][] enteredSaltAndHashBytes = decodeSaltAndHash(enteredSaltAndHash);
        byte[] enteredHash = enteredSaltAndHashBytes[1];

        byte[][] expectedSaltAndHashBytes = decodeSaltAndHash(encodedSaltAndHash);
        byte[] expectedHash = expectedSaltAndHashBytes[1];

        System.out.println("Expected: " + Hex.toHexString(expectedHash) + "\nEntered: " + Hex.toHexString(enteredHash));

        return Arrays.equals(enteredHash, expectedHash);
    }
}
