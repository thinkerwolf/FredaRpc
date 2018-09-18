package com.freda.serialization.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.freda.serialization.Serializer;

public class HessianSerializer implements Serializer {

	@Override
	public byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Hessian2Output ho = new Hessian2Output(os);
		ho.writeObject(obj);
		return os.toByteArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		Hessian2Input hi = new Hessian2Input(is);
		return (T) hi.readObject();
	}

	@Override
	public Object deserialize(byte[] bytes) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		Hessian2Input hi = new Hessian2Input(is);
		return hi.readObject();
	}

}
