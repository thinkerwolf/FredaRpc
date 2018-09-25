package com.freda;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;

public class ApacheHttpClientTest {

    public static void main(String[] args) throws Exception {
        URI uri = new URI("https://www.baidu.com");

        HttpClient client = HttpClients.createDefault();
        //client.exe
        HttpPost post = new HttpPost();
        post.setURI(uri);
        //post.set
        client.execute(post, new ResponseHandler<Object>() {
            @Override
            public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

                HttpEntity entity = response.getEntity();
                System.out.println(entity.getContentLength());

                return null;
            }
        }) ;


        HttpPost post1 = new HttpPost();
        post1.setURI(new URI("http://127.0.0.1:8089/freda/services"));
        client.execute(post1, new ResponseHandler<Object>() {
            @Override
            public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

                HttpEntity entity = response.getEntity();
                System.out.println(entity.getContentLength());

                return null;
            }
        }) ;
    }
}
