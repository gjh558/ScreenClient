package com.example.screenclient;

/**
 * Created by gengj on 7/8/2016.
 */
public class Utility {

    public static final long UINT32_MAX = 0xFFFFFFFFL;
    public static byte[] uint32ToByteArray(long src) throws IllegalArgumentException  {
        if (src < 0 || src > UINT32_MAX) {
            throw new IllegalArgumentException("the input value " + src + " is out of the range of uint32");
        }

        byte[] dest = new byte[4];
        int mask = 0xff;

        for (int i = 0; i < 4; i++) {
            dest[i] = (byte)((src >> (i * 8)) & mask);
        }

        return dest;
    }

    public static int byteArrayToUint32(byte[] src, int offset) throws IllegalArgumentException {
        int mask = 0xff;
        int temp = 0;
        int dest = 0;

        if (src.length < 4 + offset) {
            throw new IllegalArgumentException("Failed to translate " + src.length + " bytes to uint32 with " + offset + " offset");
        }

        for (int i = 0; i < 4; i++) {
            dest <<= 8;
            temp = src[offset + 3-i]&mask;
            dest |= temp;
        }

        return dest;
    }
}
