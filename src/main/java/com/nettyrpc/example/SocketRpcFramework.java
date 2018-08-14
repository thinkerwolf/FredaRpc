package com.nettyrpc.example;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * RPC服务
 *
 * @author wukai
 */
public class SocketRpcFramework {
    @SuppressWarnings("resource")
    public static void export(final Object service, int port) throws Exception {
        ServerSocket server = new ServerSocket(port);
        for (; ; ) {
            Socket socket = server.accept();
            if (socket != null) {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String methodName = ois.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) ois.readObject();
                Object[] args = (Object[]) ois.readObject();
                Method method = service.getClass().getMethod(methodName, parameterTypes);
                Object res = method.invoke(service, args);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(res);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T refer(final Class<?> clazz, final int port) throws Exception {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
            @SuppressWarnings("resource")
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket("127.0.0.1", port);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeUTF(method.getName());
                oos.writeObject(method.getParameterTypes());
                oos.writeObject(args);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object res = ois.readObject();
                System.out.println("refer proxy :: " + res);
                return null;
            }
        });
    }

}
