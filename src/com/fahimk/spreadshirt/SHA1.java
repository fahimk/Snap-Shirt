package com.fahimk.spreadshirt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import android.util.Log;

public class SHA1 {
    private static final String SHA_1 = "SHA-1";

    public static String getHashAsHex(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA_1);
            return convertToHex(md.digest(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();
            Log.e("error", "SHA-1 algorithm for signing API data not found!");
            return null;
        }
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
