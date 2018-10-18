package com.thinkerwolf.freda.serialization.jdk;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.thinkerwolf.freda.serialization.ObjectOutput;

public class JdkObjectOutput implements ObjectOutput {

	private ObjectOutputStream oos;

	public JdkObjectOutput(ObjectOutputStream oos) {
		this.oos = oos;
	}

	@Override
	public void writeObject(Object obj) throws IOException {
		oos.writeObject(obj);
	}

	@Override
	public void write(byte[] b) throws IOException {
		oos.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		oos.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		oos.flush();
	}

	@Override
	public void close() throws IOException {
		oos.close();
	}

}
