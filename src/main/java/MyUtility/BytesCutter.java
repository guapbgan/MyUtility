package MyUtility;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class BytesCutter {

    public static String rawCut(String s, int byteLength, String decode) throws UnsupportedEncodingException {
        byte[] mByte = s.getBytes(decode);
        byte[] subByte = Arrays.copyOfRange(mByte, 0, byteLength);
        return new String(subByte, decode);
    }

    /**
     * reference: https://stackoverflow.com/questions/3576754/truncating-strings-by-bytes
     * @param s
     * @param n
     * @return
     */
    public static String utf8Cut(String s, int n) {
        byte[] utf8 = s.getBytes();
        if (utf8.length < n) n = utf8.length;
        int n16 = 0;
        int advance = 1;
        int i = 0;
        while (i < n) {
            advance = 1;
            if ((utf8[i] & 0x80) == 0) i += 1;
            else if ((utf8[i] & 0xE0) == 0xC0) i += 2;
            else if ((utf8[i] & 0xF0) == 0xE0) i += 3;
            else { i += 4; advance = 2; }
            if (i <= n) n16 += advance;
        }
        return s.substring(0,n16);
    }

    /**
     * Recognizes non ascii characters to 3 bytes exactly.
     * Oracle stores chinese word as 3 bytes data.
     * !! Roughly cut, not preciously !!
     * This method cuts string to assigned length which be calculated by non ascii characters * 3 + ascii * 1.
     * @param string input string
     * @param targetByteLength assigned length
     * @return cut string
     */
    public static String oracleCut(String string, int targetByteLength){
        int byteCount = 0;
        for(int i = 0; i < string.length(); i++){
            if(string.codePointAt(i) > 127){ //127 is "~" (final ascii code)
                byteCount += 3;
            }else{
                byteCount ++;
            }
            if(byteCount > targetByteLength){
                // current i of char is exceed target length, so i - 1
                return string.substring(0, i - 1);
            }
        }
        return string;
    }
}