package com.thinkerwolf.freda.serialization.jdk;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.thinkerwolf.freda.serialization.ObjectInput;

public class JdkObjectInput implements ObjectInput {

	private ObjectInputStream ois;

	public JdkObjectInput(ObjectInputStream ois) {
		this.ois = ois;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T readObject(Class<T> t) throws ClassNotFoundException, IOException {
		return (T) ois.readObject();
	}

	@Override
	public int read() throws IOException {
		return ois.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return ois.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return ois.read(b, off, len);
	}

	@Override
	public void close() throws IOException {
		ois.close();
	}

}
