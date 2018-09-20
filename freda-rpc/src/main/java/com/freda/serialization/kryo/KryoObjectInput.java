package com.freda.serialization.kryo;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.freda.serialization.ObjectInput;

public class KryoObjectInput implements ObjectInput {

	private Kryo kryo;

	private Input input;

	public KryoObjectInput(Kryo kryo, Input input) {
		this.kryo = kryo;
		this.input = input;
	}

	@Override
	public <T> T readObject(Class<T> t) throws ClassNotFoundException, IOException {
		return kryo.readObject(input, t);
	}

	@Override
	public int read() throws IOException {
		return input.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return input.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return input.read(b, off, len);
	}

	@Override
	public void close() throws IOException {
		input.close();
	}

}
