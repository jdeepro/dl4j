package cn.edu.njupt.io;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;

public class Selector {
    public static void testChannel() throws IOException {
        Path path = Paths.get("/Users/simbaba",
                "Desktop",
                "test.txt");
        //Files.deleteIfExists(path);
        //Files.createFile(path);
        FileChannel fc =  FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        ByteBuffer bb = ByteBuffer.allocate(10);
        bb.put(new byte[]{'1','2','3',4,5,6,7,8,9,10});
        bb.flip(); /// 非常关键，不然什么都没写！！！
        fc.write(bb);
        fc.close();
    }

    public static void testSelector() throws IOException {
//        SelectableChannel
    }

    public static void main(String[] args) {
        try {
            testChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
