// Copyright 2003-2010 Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland
// www.source-code.biz, www.inventec.ch/chdh
//  GPL, GNU General Public License, V2 or later, http://www.gnu.org/licenses/gpl.html

package sample.lib;

import java.nio.charset.Charset;

public class Base64Coder {

    //Карта прервода 6-битных значений в Base64.
    private static final char[] map1 = new char[64];
    static {
        int i=0;
        for (char c='A'; c<='Z'; c++) map1[i++] = c;
        for (char c='a'; c<='z'; c++) map1[i++] = c;
        for (char c='0'; c<='9'; c++) map1[i++] = c;
        map1[i++] = '+'; map1[i++] = '/'; }

    //Карта перевода из  Base64 в 6-битные значения.
    private static final byte[] map2 = new byte[128];
    static {
        for (int i=0; i<map2.length; i++) map2[i] = -1;
        for (int i=0; i<64; i++) map2[map1[i]] = (byte)i; }


    /**
     * кодирование строки в Base64
     * @param s  Строка, которую необходимо закодировать
     * @param ch Кодировка исходной строки
     * @return   Строка, содержащая закодированные в  Base64 данные.
     */

    public static String encodeString (String s, Charset ch) {
        return new String(encode(s.getBytes(ch)));
    }

    /**
     * кодирование массива байтов в Base 64.
     * @param in  Исходный массив байтов.
     * @return    Массив 6-битных значений Base64.
     */

    public static char[] encode (byte[] in) {
        return encode(in, 0, in.length); }

    /**
     * Кодирование массива байтов в Base64
     * @param in    Массив байтов, которые необходимо закодировать
     * @param iOff  Смещение - номер первого байта, с которого начнется
     *              обработка входного массива
     * @param iLen  Колличество битов, которые необходимо закодировать
     *              от смещения
     * @return      Массив 6-битных значений Base64.
     */

    public static char[] encode (byte[] in, int iOff, int iLen) {

        int oDataLen = (iLen*4+2)/3;
        int oLen = ((iLen+2)/3)*4;
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
     * Декодирование строки из Base64
     * @param s  Строка, которую необходимо декодировать, в формате Base64
     * @param ch Кодировка закодированной строки
     * @return   Строка, содержащая декодированные из  Base64 данные.
     * @throws   IllegalArgumentException - исключение вызваное
     * неверным вводом строки, содержащим данные в формате Base64;
     */

    public static String decodeString (String s, Charset ch) {
        return new String(decode(s.toCharArray()), ch );}

    /**
     * Декодирование массива байтов из формата Base64
     * @param in Массив байтов,  который необходимо декодировать, в формате Base64
     * @return   Массив байтов, содержащий декодированные данные из формата Base64
     * @throws   IllegalArgumentException - исключение вызваное неверным вводом
     * строки, содержащим данные в формате Base64;
     */

    public static byte[] decode (char[] in) {
        return decode(in, 0, in.length);}

    /**
     * Декодирование массива байтов из Base64
     * @param in    Массив байтов, которые необходимо декодировать в формате Base64
     * @param iOff  Смещение - номер первого байта, с которого начнется обработка
     *              входного массива
     * @param iLen  Колличество битов, которые необходимо декодировать от смещения
     * @return      Массив битов, декодированных из Base64
     * @throws   IllegalArgumentException - исключение вызваное неверным вводом
     * строки, содержащим данные в формате Base64;
     */

    public static byte[] decode (char[] in, int iOff, int iLen) {

        if (iLen%4 != 0) throw new
                IllegalArgumentException ("Length of Base64 encoded input string is not a multiple of 4.");

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

            if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127) throw new
                    IllegalArgumentException ("Illegal character in Base64 encoded data.");

            b0 = map2[i0];
            b1 = map2[i1];
            b2 = map2[i2];
            b3 = map2[i3];

            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0) throw new
                    IllegalArgumentException ("Illegal character in Base64 encoded data.");

            o0 = ( b0       <<2) | (b1>>>4);
            o1 = ((b1 & 0xf)<<4) | (b2>>>2);
            o2 = ((b2 &   3)<<6) |  b3;

            out[op++] = (byte)o0;
            if (op<oLen) out[op++] = (byte)o1;
            if (op<oLen) out[op++] = (byte)o2;}
        return out; }

    private Base64Coder() {}

}