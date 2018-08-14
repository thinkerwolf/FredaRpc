package com.nettyrpc.example;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcFramework {
    /**
     * 暴漏一个接口
     */
    @SuppressWarnings("resource")
    public static void export(final Object obj, int port) throws Exception {

        ServerSocket ss = new ServerSocket(port);

        for (; ; ) {
            Socket socket = ss.accept();
            if (socket != null) {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String methodName = ois.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) ois.readObject();
                Object[] args = (Object[]) ois.readObject();
                Class<?> clazz = obj.getClass();
                Method method = clazz.getMethod(methodName, parameterTypes);
                Object res = method.invoke(obj, args);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(res);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T refer(final Class<T> clazz, final int port) {

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @SuppressWarnings("resource")
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket("127.0.0.1", port);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                oos.writeUTF(method.getName());
                oos.writeObject(method.getParameterTypes());
                oos.writeObject(args);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                return ois.readObject();
            }
        });

    }

}
