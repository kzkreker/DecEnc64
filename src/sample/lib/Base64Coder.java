package sample.lib;

import java.nio.charset.Charset;

public class Base64Coder {

    //карта прервода 6-битных значений в Base64.
    private static final char[] map1 = new char[64];
    static {
        int i=0;
        for (char c='A'; c<='Z'; c++) map1[i++] = c;
        for (char c='a'; c<='z'; c++) map1[i++] = c;
        for (char c='0'; c<='9'; c++) map1[i++] = c;
        map1[i++] = '+'; map1[i++] = '/'; }

    // карта перевода из  Base64 в 6-битные значения.
    private static final byte[] map2 = new byte[128];
    static {
        for (int i=0; i<map2.length; i++) map2[i] = -1;
        for (int i=0; i<64; i++) map2[map1[i]] = (byte)i; }


    /**
     * Encodes a string into Base64 format.
     * No blanks or line breaks are inserted.
     * @param s  A String to be encoded.
     * @return   A String containing the Base64 encoded data.
     */

    public static String encodeString (String s, Charset ch) {
        return new String(encode(s.getBytes(ch)));
    }

    /**
     * Encodes a byte array into Base64 format.
     * No blanks or line breaks are inserted in the output.
     * @param in  An array containing the data bytes to be encoded.
     * @return    A character array containing the Base64 encoded data.
     */

    public static char[] encode (byte[] in) {
        return encode(in, 0, in.length); }

    /**
     * Encodes a byte array into Base64 format.
     * No blanks or line breaks are inserted in the output.
     * @param in    An array containing the data bytes to be encoded.
     * @param iOff  Offset of the first byte in <code>in</code> to be processed.
     * @param iLen  Number of bytes to process in <code>in</code>, starting at <code>iOff</code>.
     * @return      A character array containing the Base64 encoded data.
     */

    public static char[] encode (byte[] in, int iOff, int iLen) {

        int oDataLen = (iLen*4+2)/3;       // output length without padding
        int oLen = ((iLen+2)/3)*4;         // output length including padding
        char[] out = new char[oLen];
        int ip = iOff;
        int iEnd = iOff + iLen;
        int op = 0;

        int i0,i1,i2;
        int o0,o1,o2,o3;

        while (ip < iEnd) {
            i0 = in[ip++] & 0xff;
            i1 = ip < iEnd ? in[ip++] & 0xff : 0;
            i2 = ip < iEnd ? in[ip++] & 0xff : 0;
            o0 = i0 >>> 2;
            o1 = ((i0 &   3) << 4) | (i1 >>> 4);
            o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
            o3 = i2 & 0x3F;
            out[op++] = map1[o0];
            out[op++] = map1[o1];
            out[op] = op < oDataLen ? map1[o2] : '='; op++;
            out[op] = op < oDataLen ? map1[o3] : '='; op++; }
        return out; }

    /**
     * Decodes a string from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded input data.
     * @param s  A Base64 String to be decoded.
     * @return   A String containing the decoded data.
     * @throws   IllegalArgumentException If the input is not valid Base64 encoded data.
     */

    public static String decodeString (String s, Charset ch) {
        return new String(decode(s), ch ); }

    /**
     * Decodes a byte array from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded input data.
     * @param s  A Base64 String to be decoded.
     * @return   An array containing the decoded data bytes.
     * @throws   IllegalArgumentException If the input is not valid Base64 encoded data.
     */

    public static byte[] decode (String s) {
        return decode(s.toCharArray()); }

    /**
     * Decodes a byte array from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded input data.
     * @param in  A character array containing the Base64 encoded data.
     * @return    An array containing the decoded data bytes.
     * @throws    IllegalArgumentException If the input is not valid Base64 encoded data.
     */

    public static byte[] decode (char[] in) {
        return decode(in, 0, in.length); }

    /**
     * Decodes a byte array from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded input data.
     * @param in    A character array containing the Base64 encoded data.
     * @param iOff  Offset of the first character in <code>in</code> to be processed.
     * @param iLen  Number of characters to process in <code>in</code>, starting at <code>iOff</code>.
     * @return      An array containing the decoded data bytes.
     * @throws      IllegalArgumentException If the input is not valid Base64 encoded data.
     */

    public static byte[] decode (char[] in, int iOff, int iLen) {

        if (iLen%4 != 0) throw new IllegalArgumentException ("Length of Base64 encoded input string is not a multiple of 4.");

        int oLen, ip,iEnd,op;
        int i0,i1,i2,i3;
        int b0,b1,b2,b3;
        int o0,o1,o2;

        byte[] out;

        while (iLen > 0 && in[iOff+iLen-1] == '=') iLen--;

        oLen = (iLen*3) / 4;
        out = new byte[oLen];
        ip = iOff;
        iEnd = iOff + iLen;
        op = 0;

        while (ip < iEnd) {

            i0 = in[ip++];
            i1 = in[ip++];
            i2 = ip < iEnd ? in[ip++] : 'A';
            i3 = ip < iEnd ? in[ip++] : 'A';

            if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127) throw new IllegalArgumentException ("Illegal character in Base64 encoded data.");

            b0 = map2[i0];
            b1 = map2[i1];
            b2 = map2[i2];
            b3 = map2[i3];

            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0) throw new IllegalArgumentException ("Illegal character in Base64 encoded data.");

            o0 = ( b0       <<2) | (b1>>>4);
            o1 = ((b1 & 0xf)<<4) | (b2>>>2);
            o2 = ((b2 &   3)<<6) |  b3;

            out[op++] = (byte)o0;
            if (op<oLen) out[op++] = (byte)o1;
            if (op<oLen) out[op++] = (byte)o2;
            }
        return out; }

    private Base64Coder() {}

}