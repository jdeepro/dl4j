package cn.edu.njupt.io;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

public class AIO {

    public static void testSocketWeb() throws IOException {
        Socket socket=new Socket(InetAddress.getByName("www.baidu.com"),80);
        OutputStream os=socket.getOutputStream();
        OutputStreamWriter osw=new OutputStreamWriter(os);
        BufferedWriter bw=new BufferedWriter(osw);

        //最基本的请求头
        bw.write("GET / HTTP/1.1\r\n");
        bw.write("Host: www.baidu.com\r\n\r\n");
        bw.flush();
        socket.shutdownOutput();

        //读取response
        InputStream is=socket.getInputStream();
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        String str=null;
        while((str=br.readLine())!=null){
            System.out.println(str);
        }
        socket.close();
    }

    public static void testURLWeb() throws IOException {
        URL url=new URL("http://www.baidu.com");
        InputStream is= url.openStream();

        //读取response
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        String str=null;
        while((str=br.readLine())!=null){
            System.out.println(str);
        }
        is.close();
    }

    public static void testHttpClientWeb() {
        HttpGet http=new HttpGet("https://www.baidu.com");
        try (
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(http);
        ) {
            HttpEntity entity = response.getEntity();
            String page = EntityUtils.toString(entity, "UTF-8");
            System.out.println(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

            testHttpClientWeb();

    }
}
