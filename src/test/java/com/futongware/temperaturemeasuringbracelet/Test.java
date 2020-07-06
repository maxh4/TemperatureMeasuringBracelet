package com.futongware.temperaturemeasuringbracelet;

import org.apache.commons.lang3.StringUtils;

public class Test {
    public static void main(String[] args) {
//        for (int i = 1; i < 200; i++) {
            try {
                String revdDataStr = "0000000A1800029F12090E08EFC500000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
                //String revdDataStr = "029F12090E08EFC5000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000A1800";
                byte[] revddata = convertToHex(revdDataStr);
                int wordCount = 1;
                Tagtemperture_DATA tagtempertureData = new Tagtemperture_DATA(revddata, wordCount);
                byte[] temperdata = tagtempertureData.Temperdata();
                System.out.println("[i=" + 1 + "]" + convertToString(temperdata));
                byte[] epcdata = tagtempertureData.tagepc;
                System.out.println(convertToString(epcdata));
            } catch (Exception e) {}
//        }
    }

    public static void Hex2Str(byte[] buf, int len, char[] out) {
        char[] hexc = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        for(int i = 0; i < len; ++i) {
            out[i * 2] = hexc[(buf[i] & 255) / 16];
            if (i * 2 + 1 < out.length) {
                out[i * 2 + 1] = hexc[(buf[i] & 255) % 16];
            }
        }
    }

    public static void Str2Hex(String buf, int len, byte[] hexbuf) {
        String chex = "0123456789ABCDEF";
        if (len != 0) {
            for(int i = 0; i < len; i += 2) {
                byte hnx = (byte)chex.indexOf(buf.toUpperCase().substring(i, i + 1));
                byte lnx = 0;
                if (i + 2 <= len) {
                    lnx = (byte)chex.indexOf(buf.toUpperCase().substring(i + 1, i + 2));
                }
                hexbuf[i % 2 == 0 ? i / 2 : i / 2 + 1] = (byte)(hnx << 4 & 255 | lnx & 255);
            }
        }
    }

    private static String convertToString(byte[] data, int dataLen) {
        if (data == null) return null;
        if (data.length == 0) return "";
        char[] chrArr = new char[dataLen * 2];
        Hex2Str(data, dataLen, chrArr);
        return new String(chrArr);
    }
    private static String convertToString(byte[] data) {
        return convertToString(data, data.length);
    }
    private static byte[] convertToHex(String text) {
        if (text == null || StringUtils.isEmpty(text)) return null;
        text = text.trim();
        byte[] datab = new byte[text.length()];
        Str2Hex(text, text.length(), datab);
        return datab;
    }
}
