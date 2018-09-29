package com.example.dell.benchtest;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by dell on 2018/9/19.
 */

public class CRC {
    public int byte2ToUnsignedShort(byte[] bytes, int off) {

        int high = bytes[off];

        int low = bytes[off + 1];

        return (high << 8 & 0xFF00) | (low & 0xFF);

    }
    public int bytesToInt(byte[] src, int offset) {

        int value;
        value = (int) ((src[offset] & 0xFF)

                | ((src[offset+1] & 0xFF)<<8)

                | ((src[offset+2] & 0xFF)<<16)

                | ((src[offset+3] & 0xFF)<<24));

        return value;

    }



    /**

     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用

     */

    public int bytesToInt2(byte[] src, int offset) {

        int value;

        value = (int) ( ((src[offset] & 0xFF)<<24)

                |((src[offset+1] & 0xFF)<<16)

                |((src[offset+2] & 0xFF)<<8)

                |(src[offset+3] & 0xFF));

        return value;

    }
    public byte[] intToBytes( int value )

    {

        byte[] src = new byte[4];

        src[3] =  (byte) ((value>>24) & 0xFF);

        src[2] =  (byte) ((value>>16) & 0xFF);

        src[1] =  (byte) ((value>>8) & 0xFF);

        src[0] =  (byte) (value & 0xFF);

        return src;

    }
    public long bytetolong(byte[] res) {
        int firstByte = (0x000000FF & ((int)res[0]));
        int secondByte = (0x000000FF & ((int)res[1]));
        int thirdByte = (0x000000FF & ((int)res[2]));
        int fourthByte = (0x000000FF & ((int)res[3]));
        long result = ((long) (firstByte << 24
                | secondByte << 16
                | thirdByte << 8
                | fourthByte))
                & 0xFFFFFFFFL;
        return result;
    }

    /**

     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用

     */

    public byte[] intToBytes2(int value)

    {

        byte[] src = new byte[4];

        src[0] = (byte) ((value>>24) & 0xFF);

        src[1] = (byte) ((value>>16)& 0xFF);

        src[2] = (byte) ((value>>8)&0xFF);

        src[3] = (byte) (value & 0xFF);

        return src;

    }
    public short[] toShortArray(byte[] src) {

        int count = src.length >> 1;
        short[] dest = new short[count];
        for (int i = 0; i < count; i++) {
            dest[i] = (short) (src[i * 2] << 8 | src[2 * i + 1] & 0xff);
        }
        return dest;
    }

}
