package com.google.getsign;

import java.security.MessageDigest;

/**
 * Created by Administrator on 2017/5/29 0029.
 */

class MD5 {
    private MD5() {
        super();
    }

    public static final String getMessageDigest(byte[] arg11) {
        char[] v2 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest v8 = MessageDigest.getInstance("MD5");
            v8.update(arg11);
            byte[] v7 = v8.digest();
            int v4 = v7.length;
            char[] v9 = new char[v4 * 2];
            int v3 = 0;
            int v6 = 0;
            while(v3 < v4) {
                int v0 = v7[v3];
                int v5 = v6 + 1;
                v9[v6] = v2[v0 >>> 4 & 15];
                v6 = v5 + 1;
                v9[v5] = v2[v0 & 15];
                ++v3;
            }

            String v10 = new String(v9);
            return v10;
        }
        catch(Exception v1) {
            return null;
        }
    }

    public static final byte[] getRawDigest(byte[] arg3) {
        byte[] v2;
        try {
            MessageDigest v1 = MessageDigest.getInstance("MD5");
            v1.update(arg3);
            v2 = v1.digest();
        }
        catch(Exception v0) {
            v2 = null;
        }

        return v2;
    }
}
