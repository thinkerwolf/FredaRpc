package com.freda.serialization.hessian;

import java.io.*;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.freda.serialization.Serializer;

public class HessianSerializer implements Serializer {

	@Override
	public ObjectOutput serialize(OutputStream os) throws IOException {
		return new HessianObjectOutput(new HessianOutput(os));
	}

	@Override
	public ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException {
		return new HessianObjectInput(new HessianInput(is));
	}
}
