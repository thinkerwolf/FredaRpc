package com.freda.rpc.http;

import com.freda.common.Constants;
import com.freda.common.concurrent.DefaultPromise;
import com.freda.remoting.RequestMessage;
import com.freda.rpc.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpInvoker<T> extends AbstractInvoker<T> {

    private URL[] urls;
    private AtomicInteger round = new AtomicInteger(0);

    private HttpClient httpClient = HttpClients.createDefault();
    private HttpPost[] requests;


    public HttpInvoker(String id, Class<T> type, URL[] urls) {
        super(id, type);
        this.urls = urls;
        requests = new HttpPost[urls.length];
        for (int i = 0; i < urls.length; i++) {
            try {
                requests[i] = new HttpPost(urls[i].toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Result invoke(RequestMessage inv) throws RpcException {
        return invoke(inv, false);
    }

    @Override
    public Result invoke(final RequestMessage inv, final boolean isAsync) throws RpcException {

        HttpPost post = null;
        try {
            if (requests.length == 1) {
                post = requests[0];
            } else {
                post = requests[round.getAndIncrement() % requests.length];
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(inv);
            HttpEntity entity = new ByteArrayEntity(baos.toByteArray());
            post.setEntity(entity);
            if (isAsync) {
                final Context context = Context.getContext();
                final DefaultPromise<Object> promise = new DefaultPromise<>();
                context.setCurrent(inv, promise);
                httpClient.execute(post, new ResponseHandler<Object>() {
                    @Override
                    public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                        return HttpInvoker.this.handleResponse(response, promise);
                    }
                });
                return ResultBuilder.buildSuccessResult(null);
            } else {
                return handleResponse(httpClient.execute(post), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBuilder.buildFailResult();
        }

        /*HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            URL url = null;
            if (urls.length == 1) {
                url = urls[0];
            } else {
                url = urls[round.getAndIncrement() % urls.length];
            }
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接请求方式
            connection.setRequestMethod("POST");
            // 设置连接主机服务器超时时间：15000毫秒
            connection.setConnectTimeout(inv.getParameter(Constants.TIMEOUT, Constants.DEFAULT_TIMEOUT));
            // 设置读取主机服务器返回数据超时时间：60000毫秒
            connection.setReadTimeout(60000);
            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            // 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
            connection.setDoInput(true);
            // 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式。
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 设置鉴权信息：Authorization: Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0
            connection.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
            // 通过连接对象获取一个输出流
            os = connection.getOutputStream();
            // 通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(inv);
            os.write(baos.toByteArray());
            oos.close();
            baos.close();
            // 通过连接对象获取一个输入流，向远程读取
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                is = connection.getInputStream();
                // 对输入流对象进行包装:charset根据工作项目组的要求来设置
                ObjectInputStream ois = new ObjectInputStream(is);
                Object result = ois.readObject();
                ois.close();
                return ResultBuilder.buildSuccessResult(result);
            } else {
                return ResultBuilder.buildFailResult();
            }
        } catch (Exception e) {
            return ResultBuilder.buildFailResult();
        } finally {
            // 关闭资源
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            connection.disconnect();
        }*/
    }


    private Result handleResponse(HttpResponse response, DefaultPromise<Object> promise) {
        try {
            if (response.getStatusLine().getStatusCode() == 200) {
                ObjectInputStream ois = new ObjectInputStream(response.getEntity().getContent());
                Object result = ois.readObject();
                ois.close();
                if (promise != null) promise.setSuccess(result);
                return ResultBuilder.buildSuccessResult(result);
            } else {
                if (promise != null) promise.setFailure(new RuntimeException("http response status code error"));

                return ResultBuilder.buildFailResult();
            }

        } catch (Exception e) {
            if (promise != null) promise.setFailure(e);
            return ResultBuilder.buildFailResult();
        }
    }


    @Override
    public synchronized void destory() {
        if (destory) {
            return;
        }
        destory = true;
        if (urls != null) {
            for (int i = 0; i < urls.length; i++) {
                requests[i] = null;
                urls[i] = null;
            }
            urls = null;
            requests = null;
        }
    }

}
