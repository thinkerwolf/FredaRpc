package com.freda.serialization.jdk;

import java.io.*;

import com.freda.serialization.Serializer;

public class JdkSerializer implements Serializer {

    @Override
    public ObjectOutput serialize(OutputStream os) throws IOException {
	    ObjectOutputStream oo = new ObjectOutputStream(os);
        return oo;
    }

    @Override
    public ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream oi = new ObjectInputStream(is);
        return oi;
    }
}
