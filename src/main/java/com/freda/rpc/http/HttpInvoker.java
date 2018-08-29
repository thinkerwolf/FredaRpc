package com.freda.rpc.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import com.freda.remoting.RequestMessage;
import com.freda.rpc.AbstractInvoker;
import com.freda.rpc.RpcException;

public class HttpInvoker<T> extends AbstractInvoker<T> {

	private URL[] urls;
	private AtomicInteger round = new AtomicInteger(0);

	public HttpInvoker(String id, Class<T> type, URL[] urls) {
		super(id, type);
		this.urls = urls;
	}

	@Override
	public Object invoke(RequestMessage inv) throws RpcException {
		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream os = null;
		Object result = null;
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
			connection.setConnectTimeout(15000);
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
				result = ois.readObject();
				ois.close();
			} else {
				throw new RpcException("rpc http invoke error, errCode = [" + responseCode + "]");
			}
		} catch (Exception e) {
			throw new RpcException("rpc http invoke exception", e);
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
		}
		return result;
	}

}
