package fixers.jBugger.Utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encryption {

    public static String encrypt(String password) {

        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest md;
            md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();

            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            return sb.toString();
        }

    }

}