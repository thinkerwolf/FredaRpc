package com.freda.rpc.http;

import com.freda.common.Net;
import com.freda.common.ServiceLoader;
import com.freda.common.concurrent.DefaultPromise;
import com.freda.rpc.*;
import com.freda.serialization.Serializer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpInvoker<T> extends AbstractInvoker<T> {

    //private List<Net> ncs;
    private AtomicInteger round = new AtomicInteger(0);

    private HttpClient httpClient = HttpClients.createDefault();
    private HttpPost[] requests;
    private Net[] nets;

    public HttpInvoker(String id, Class<T> type, Net[] nets) {
        super(id, type);
        this.nets = nets;
        requests = new HttpPost[nets.length];
        for (int i = 0; i < nets.length; i++) {
            try {
                URI uri = new URI(nets[i].getPath() + HttpProtocol.CONTEXT_PATH + "/" + id + "?serialization=" + nets[i].getSerialization());
                requests[i] = new HttpPost(uri);
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
        Net net = null;
        try {
            if (requests.length == 1) {
                post = requests[0];
                net = nets[0];
            } else {
                int r = round.getAndIncrement() % requests.length;
                post = requests[r];
                net = nets[r];
            }
            final Serializer serializer = ServiceLoader.getService(net.getSerialization(), Serializer.class);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput oo = serializer.serialize(baos);
            oo.writeObject(inv);
            oo.close();
            HttpEntity entity = new ByteArrayEntity(baos.toByteArray());
            post.setEntity(entity);
            if (isAsync) {
                final Context context = Context.getContext();
                final DefaultPromise<Object> promise = new DefaultPromise<>();
                context.setCurrent(inv, promise);
                httpClient.execute(post, new ResponseHandler<Object>() {
                    @Override
                    public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                        return HttpInvoker.this.handleResponse(response, promise, serializer);
                    }
                });
                return ResultBuilder.buildSuccessResult(null);
            } else {
                return handleResponse(httpClient.execute(post), null, serializer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBuilder.buildFailResult();
        }
    }


    private Result handleResponse(HttpResponse response, DefaultPromise<Object> promise, Serializer serializer) {
        try {
            if (response.getStatusLine().getStatusCode() == 200) {
            	ObjectInput oi = serializer.deserialize(response.getEntity().getContent());
                Object result = oi.readObject();
                oi.close();
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
        if (requests != null) {
            for (int i = 0; i < requests.length; i++) {
                requests[i] = null;
            }
            requests = null;
        }
    }

}
