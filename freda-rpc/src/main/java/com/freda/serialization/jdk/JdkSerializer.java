package com.freda.serialization.jdk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.freda.serialization.Serializer;

public class JdkSerializer implements Serializer {

	@Override
	public byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(obj);
		return os.toByteArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(is);
		return (T) ois.readObject();
	}
	
	@Override
	public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(is);
		return ois.readObject();
	}

}
