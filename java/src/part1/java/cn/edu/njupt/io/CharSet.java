package cn.edu.njupt.io;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.HexDump;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class CharSet {
    static void testEncoding() {
        File file = new File("/Users/simbaba/Desktop/联通");

        try (FileInputStream fis = new FileInputStream(file)) {
            int len = fis.available();
            byte[] bytes = new byte[len];
            fis.read(bytes);

            System.out.println(Hex.encodeHexString(bytes));
            String text = new String(bytes, Charset.forName("UTF-8"));
            System.out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void testEncodingErr() throws UnsupportedEncodingException {
        String str = "张三，10岁";
        byte[] bytes = str.getBytes("GBK");

        System.out.println(Hex.encodeHexString(bytes));
        String text = new String(bytes, Charset.forName("UTF-8"));
        System.out.println(text);
    }

    public static void main(String[] args) {
        try {
            testEncodingErr();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}