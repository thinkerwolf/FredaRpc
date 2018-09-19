package com.freda.serialization.fastjson;

import java.io.IOException;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.freda.serialization.ObjectOutput;

public class FastjsonObjectOutput implements ObjectOutput {

	JSONSerializer serializer;

	public FastjsonObjectOutput(JSONSerializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public void writeObject(Object obj) throws IOException {
		serializer.write(obj);
	}

	@Override
	public void write(byte[] b) throws IOException {
		serializer.getWriter().writeByteArray(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		throw new UnsupportedOperationException("write");
	}

	@Override
	public void flush() throws IOException {
		serializer.getWriter().flush();
	}

	@Override
	public void close() throws IOException {
		serializer.close();
	}

}
